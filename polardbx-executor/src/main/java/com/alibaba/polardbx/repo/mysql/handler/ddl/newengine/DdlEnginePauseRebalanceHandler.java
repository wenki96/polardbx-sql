/*
 * Copyright [2013-2021], Alibaba Group Holding Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alibaba.polardbx.repo.mysql.handler.ddl.newengine;

import com.alibaba.polardbx.common.exception.TddlRuntimeException;
import com.alibaba.polardbx.common.exception.code.ErrorCode;
import com.alibaba.polardbx.common.properties.ConnectionParams;
import com.alibaba.polardbx.common.utils.logger.Logger;
import com.alibaba.polardbx.executor.cursor.Cursor;
import com.alibaba.polardbx.executor.cursor.impl.AffectRowCursor;
import com.alibaba.polardbx.executor.ddl.newengine.DdlEngineRequester;
import com.alibaba.polardbx.executor.spi.IRepository;
import com.alibaba.polardbx.gms.metadb.misc.DdlEngineRecord;
import com.alibaba.polardbx.optimizer.context.ExecutionContext;
import com.alibaba.polardbx.optimizer.core.rel.dal.LogicalDal;
import com.alibaba.polardbx.statistics.SQLRecorderLogger;
import org.apache.calcite.sql.SqlPauseRebalanceJob;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;

import static com.alibaba.polardbx.common.ddl.newengine.DdlType.ALTER_TABLEGROUP;
import static com.alibaba.polardbx.common.ddl.newengine.DdlType.MOVE_DATABASE;
import static com.alibaba.polardbx.common.ddl.newengine.DdlType.REBALANCE;

/**
 * @author wumu
 */
public class DdlEnginePauseRebalanceHandler extends DdlEngineJobsHandler {

    private final static Logger LOG = SQLRecorderLogger.ddlEngineLogger;

    public DdlEnginePauseRebalanceHandler(IRepository repo) {
        super(repo);
    }

    @Override
    public Cursor doHandle(final LogicalDal logicalPlan, ExecutionContext executionContext) {
        SqlPauseRebalanceJob command = (SqlPauseRebalanceJob) logicalPlan.getNativeSqlNode();
        if (command.isAll()) {
            throw new TddlRuntimeException(ErrorCode.ERR_DDL_JOB_ERROR, "Operation on multi ddl jobs is not allowed");
        }

        if (command.getJobIds() == null || command.getJobIds().isEmpty()) {
            return new AffectRowCursor(0);
        }

        if (command.getJobIds().size() > 1) {
            throw new TddlRuntimeException(ErrorCode.ERR_DDL_JOB_ERROR, "Operation on multi ddl jobs is not allowed");
        }

        return doPause(command.getJobIds().get(0), executionContext);
    }

    public Cursor doPause(Long jobId, ExecutionContext executionContext) {
        boolean enableOperateSubJob =
            executionContext.getParamManager().getBoolean(ConnectionParams.ENABLE_OPERATE_SUBJOB);

        boolean enableContinueRunningSubJob =
            executionContext.getParamManager().getBoolean(ConnectionParams.ENABLE_CONTINUE_RUNNING_SUBJOB);

        DdlEngineRecord record = schedulerManager.fetchRecordByJobId(jobId);

        if (record == null) {
            throw new TddlRuntimeException(ErrorCode.ERR_DDL_JOB_ERROR, "The ddl job does not exist");
        }

        if (!REBALANCE.name().equalsIgnoreCase(record.ddlType)
            && !ALTER_TABLEGROUP.name().equalsIgnoreCase(record.ddlType)
            && !MOVE_DATABASE.name().equalsIgnoreCase(record.ddlType)) {
            throw new TddlRuntimeException(ErrorCode.ERR_DDL_JOB_ERROR,
                "Operation on non-rebalance job is not allowed");
        }

        int countDone =
            DdlEngineRequester.pauseJob(record, enableOperateSubJob, enableContinueRunningSubJob, executionContext);

        boolean asyncPause = executionContext.getParamManager().getBoolean(ConnectionParams.PURE_ASYNC_DDL_MODE)
            || executionContext.getParamManager().getBoolean(ConnectionParams.ASYNC_PAUSE);
        if (!asyncPause) {
            try {
                respond(record.schemaName, record.jobId, executionContext, false, true);
            } catch (RuntimeException e) {
                // ignore
            }
        }

        return new AffectRowCursor(countDone);
    }
}

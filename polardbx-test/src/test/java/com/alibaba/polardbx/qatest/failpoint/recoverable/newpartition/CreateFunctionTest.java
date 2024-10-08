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

package com.alibaba.polardbx.qatest.failpoint.recoverable.newpartition;

import com.alibaba.polardbx.executor.utils.failpoint.FailPointKey;
import com.alibaba.polardbx.qatest.util.JdbcUtil;
import net.jcip.annotations.NotThreadSafe;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.ResultSet;
import java.sql.SQLException;

@NotThreadSafe
public class CreateFunctionTest extends BasePartitionedTableFailPointTestCase {
    String createFunctionStmt =
        "CREATE FUNCTION %s() RETURNS CHAR(10) no sql  RETURN \"\"\n";

    String dropFunctionStmt = "DROP FUNCTION IF EXISTS %s";

    String showCreateFunction = "show create function %s";

    String functionName = "func_test_create";

    @Before
    public void doBefore() {
        clearFailPoints();
        JdbcUtil.executeSuccess(failPointConnection, String.format(dropFunctionStmt, functionName));
    }

    @After
    public void doAfter() {
        clearFailPoints();
        JdbcUtil.executeSuccess(failPointConnection, String.format(dropFunctionStmt, functionName));
    }

    @Test
    public void test_FP_EACH_DDL_TASK_BACK_AND_FORTH() {
        enableFailPoint(FailPointKey.FP_EACH_DDL_TASK_BACK_AND_FORTH, "true");
        execDdlWithFailPoints();
    }

    @Test
    public void test_FP_EACH_DDL_TASK_FAIL_ONCE() {
        enableFailPoint(FailPointKey.FP_EACH_DDL_TASK_FAIL_ONCE, "true");
        execDdlWithFailPoints();
    }

    @Test
    public void test_FP_EACH_DDL_TASK_EXECUTE_TWICE() {
        enableFailPoint(FailPointKey.FP_EACH_DDL_TASK_EXECUTE_TWICE, "true");
        execDdlWithFailPoints();
    }

    @Test
    public void test_FP_RANDOM_PHYSICAL_DDL_EXCEPTION() {
        enableFailPoint(FailPointKey.FP_RANDOM_PHYSICAL_DDL_EXCEPTION, "30");
        execDdlWithFailPoints();
    }

    @Test
    public void test_FP_RANDOM_FAIL() {
        enableFailPoint(FailPointKey.FP_RANDOM_FAIL, "30");
        execDdlWithFailPoints();
    }

    @Test
    public void test_FP_RANDOM_SUSPEND() {
        enableFailPoint(FailPointKey.FP_RANDOM_SUSPEND, "30,3000");
        execDdlWithFailPoints();
    }

    protected void execDdlWithFailPoints() {
        try {
            createFunction();
        } catch (SQLException e) {
            Assert.fail("execute create procedure failed!");
        }
    }

    protected void createFunction() throws SQLException {
        JdbcUtil.executeUpdateSuccess(failPointConnection, String.format(createFunctionStmt, functionName));

        String showCreateTableString = showCreateProcedure(functionName);
        Assert.assertTrue(("mysql." + functionName).equalsIgnoreCase(showCreateTableString));
        JdbcUtil.executeSuccess(failPointConnection, String.format("select %s()", functionName));

        JdbcUtil.executeSuccess(failPointConnection, String.format(dropFunctionStmt, functionName));
        JdbcUtil.executeFailed(failPointConnection, String.format(showCreateFunction, functionName),
            "ERR_UDF_NOT_FOUND");
    }

    private String showCreateProcedure(String procedureName) throws SQLException {
        try (ResultSet rs = JdbcUtil.executeQuerySuccess(failPointConnection,
            String.format(showCreateFunction, procedureName))) {
            if (rs.next()) {
                return rs.getString("function");
            }
            return null;
        }
    }
}

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

package org.apache.calcite.sql;

import com.google.common.collect.Lists;
import org.apache.calcite.sql.parser.SqlParserPos;

import java.util.ArrayList;
import java.util.List;

public class SqlShowMasterStatus extends SqlShow {
    private SqlNode with;
    private static final List<SqlNode> OPERANDS_EMPTY = new ArrayList<>(0);
    private static final List<SqlSpecialIdentifier> SPECIAL_IDENTIFIERS = Lists.newArrayList(
        SqlSpecialIdentifier.MASTER,
        SqlSpecialIdentifier.STATUS);

    public SqlShowMasterStatus(SqlParserPos pos) {
        super(pos, SPECIAL_IDENTIFIERS);
    }

    public SqlShowMasterStatus(SqlParserPos pos, SqlNode with) {
        super(pos, SPECIAL_IDENTIFIERS);
        this.with = with;
    }

    @Override
    public SqlKind getShowKind() {
        return SqlKind.SHOW_MASTER_STATUS;
    }

    public SqlNode getWith() {
        return with;
    }
}

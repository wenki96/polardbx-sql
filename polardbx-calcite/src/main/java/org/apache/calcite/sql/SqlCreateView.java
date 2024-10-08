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

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import org.apache.calcite.adapter.elasticsearch.ElasticsearchRel;
import org.apache.calcite.jdbc.CalcitePrepare;
import org.apache.calcite.jdbc.CalciteSchema;
import org.apache.calcite.schema.Function;
import org.apache.calcite.schema.SchemaPlus;
import org.apache.calcite.schema.TranslatableTable;
import org.apache.calcite.schema.impl.ViewTable;
import org.apache.calcite.schema.impl.ViewTableMacro;
import org.apache.calcite.sql.dialect.CalciteSqlDialect;
import org.apache.calcite.sql.parser.SqlParserPos;
import org.apache.calcite.sql.validate.SqlValidator;
import org.apache.calcite.sql.validate.SqlValidatorScope;
import org.apache.calcite.util.ImmutableNullableList;

import java.util.List;


/**
 * add create view support
 *
 * @author hongxi.chx
 * @create 2018-05-07 16:17
 */
public class SqlCreateView extends SqlCreate {
    private SqlNodeList columnList;
    private SqlNode query;

    private static final SqlOperator OPERATOR =
            new SqlSpecialOperator("CREATE VIEW", SqlKind.CREATE_VIEW);

    private boolean alter;

    /** Creates a SqlCreateView. */
    public SqlCreateView(SqlParserPos pos, boolean replace, boolean alter, SqlIdentifier name,
                  SqlNodeList columnList, SqlNode query) {
        super(OPERATOR, pos, replace, false);
        this.alter = alter;
        this.name = Preconditions.checkNotNull(name);
        this.columnList = columnList; // may be null
        this.query = Preconditions.checkNotNull(query);
    }

    public List<SqlNode> getOperandList() {
        return ImmutableNullableList.of(name, columnList, query);
    }

    @Override public void unparse(SqlWriter writer, int leftPrec, int rightPrec) {
        if (getReplace()) {
            writer.keyword("CREATE OR REPLACE");
        } else {
            writer.keyword("CREATE");
        }
        writer.keyword("VIEW");
        name.unparse(writer, leftPrec, rightPrec);
        if (columnList != null && columnList.size() > 0) {
            SqlWriter.Frame frame = writer.startList("(", ")");
            for (SqlNode c : columnList) {
                writer.sep(",");
                c.unparse(writer, 0, 0);
            }
            writer.endList(frame);
        }
        writer.keyword("AS");
        writer.newlineAndIndent();
        query.unparse(writer, 0, 0);
    }

    @Override
    public void validate(SqlValidator validator, SqlValidatorScope scope) {
        validator.validateCreateView(this, scope);
    }

    public SqlNode getQuery() {
        return query;
    }

    public SqlNodeList getColumnList() {return columnList;}

    @Override
    public SqlIdentifier getName() {
        return (SqlIdentifier) name;
    }

    public boolean isAlter() {
        return alter;
    }
}

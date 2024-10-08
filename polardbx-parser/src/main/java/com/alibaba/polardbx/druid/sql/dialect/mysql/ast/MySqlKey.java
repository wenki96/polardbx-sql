/*
 * Copyright 1999-2017 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.polardbx.druid.sql.dialect.mysql.ast;

import com.alibaba.polardbx.druid.DbType;
import com.alibaba.polardbx.druid.sql.ast.SQLExpr;
import com.alibaba.polardbx.druid.sql.ast.SQLIndex;
import com.alibaba.polardbx.druid.sql.ast.SQLName;
import com.alibaba.polardbx.druid.sql.ast.statement.SQLTableConstraint;
import com.alibaba.polardbx.druid.sql.ast.statement.SQLUnique;
import com.alibaba.polardbx.druid.sql.ast.statement.SQLUniqueConstraint;
import com.alibaba.polardbx.druid.sql.dialect.mysql.visitor.MySqlASTVisitor;
import com.alibaba.polardbx.druid.sql.visitor.SQLASTVisitor;

public class MySqlKey extends SQLUnique implements SQLUniqueConstraint, SQLTableConstraint, SQLIndex {

    public MySqlKey() {
        dbType = DbType.mysql;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor instanceof MySqlASTVisitor) {
            accept0((MySqlASTVisitor) visitor);
        }
    }


    protected void accept0(MySqlASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.getName());
            acceptChild(visitor, this.getColumns());
            acceptChild(visitor, this.getName());
        }
        visitor.endVisit(this);
    }

    public String getIndexType() {
        return indexDefinition.getOptions().getIndexType();
    }

    public void setIndexType(String indexType) {
        indexDefinition.getOptions().setIndexType(indexType);
    }

    public boolean isHasConstraint() {
        return indexDefinition.hasConstraint();
    }

    public void setHasConstraint(boolean hasConstraint) {
        indexDefinition.setHasConstraint(hasConstraint);
    }

    public void cloneTo(MySqlKey x) {
        super.cloneTo(x);
    }

    public MySqlKey clone() {
        MySqlKey x = new MySqlKey();
        cloneTo(x);
        return x;
    }

    public SQLExpr getKeyBlockSize() {
        return indexDefinition.getOptions().getKeyBlockSize();
    }

    public void setKeyBlockSize(SQLExpr x) {
        indexDefinition.getOptions().setKeyBlockSize(x);
    }

    public SQLName getTableGroup() {
        return this.indexDefinition.getTableGroup();
    }

    public void setTableGroup(SQLName tableGroup) {
        this.indexDefinition.setTableGroup(tableGroup);
    }

    public boolean isWithImplicitTablegroup() {
        return this.indexDefinition.isWithImplicitTablegroup();
    }

    public void setWithImplicitTablegroup(boolean withImplicitTablegroup) {
        this.indexDefinition.setWithImplicitTablegroup(withImplicitTablegroup);
    }

}

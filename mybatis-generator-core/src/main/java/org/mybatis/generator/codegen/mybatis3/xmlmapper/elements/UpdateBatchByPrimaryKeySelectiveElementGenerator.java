/**
 *    Copyright 2006-2020 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.mybatis.generator.codegen.mybatis3.xmlmapper.elements;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.ListUtilities;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;
import org.mybatis.generator.config.PropertyRegistry;

/**
 * modified
 * UpdateBatch
 *
 * @author xiangwb
 */
public class UpdateBatchByPrimaryKeySelectiveElementGenerator extends
        AbstractXmlElementGenerator {

    public UpdateBatchByPrimaryKeySelectiveElementGenerator() {
        super();
    }

    @Override
    public void addElements(XmlElement parentElement) {
        XmlElement answer = new XmlElement("update");
        answer.addAttribute(new Attribute("id", introspectedTable.getUpdateBatchByPrimaryKeyStatementId()));
//        String parameterType;
//
//        if (introspectedTable.getRules().generateRecordWithBLOBsClass()) {
//            parameterType = introspectedTable.getRecordWithBLOBsType();
//        } else {
//            parameterType = introspectedTable.getBaseRecordType();
//        }
//
//        answer.addAttribute(new Attribute("parameterType", //$NON-NLS-1$
//                parameterType));
        context.getCommentGenerator().addComment(answer);

        IntrospectedColumn primaryKeyColumn = introspectedTable.getPrimaryKeyColumns().get(0);
        String keyColumnName = MyBatis3FormattingUtilities.getEscapedColumnName(primaryKeyColumn);
        String keyParam = MyBatis3FormattingUtilities.getBatchParameterClause(primaryKeyColumn);

        StringBuilder ifSb = new StringBuilder();
        StringBuilder sb = new StringBuilder();
        sb.append("update ");
//        sb.append(introspectedTable.getFullyQualifiedTableNameAtRuntime());
        answer.addElement(new TextElement(sb.toString()));
        answer.addElement(getTable());//替换table

        XmlElement trims = new XmlElement("trim");
        trims.addAttribute(new Attribute("prefix", "set"));
        trims.addAttribute(new Attribute("suffixOverrides", ","));
        answer.addElement(trims);

        String createTime = context.getProperty(PropertyRegistry.COMMENT_CREATE_TIME);
        String ignore = context.getProperty(PropertyRegistry.UPDATE_IGNORE);
        String modifiedTime = context.getProperty(PropertyRegistry.COMMENT_MODIFIED_TIME);

        for (IntrospectedColumn introspectedColumn : ListUtilities.removeGeneratedAlwaysColumns(introspectedTable
                .getNonPrimaryKeyColumns())) {
            String columnName = MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn);
            //忽略createTime，ignore
            if ((createTime != null && createTime.equalsIgnoreCase(columnName)) || (ignore != null && ignore.equalsIgnoreCase(columnName))) {
                continue;
            }
            sb.setLength(0);
            sb.append(columnName);
            sb.append("=case");
            XmlElement trim = new XmlElement("trim");
            trim.addAttribute(new Attribute("prefix", sb.toString()));
            trim.addAttribute(new Attribute("suffix", "end,"));
            trims.addElement(trim);

            XmlElement foreach = new XmlElement("foreach");
            foreach.addAttribute(new Attribute("collection", "list"));
            foreach.addAttribute(new Attribute("item", "obj"));
            trim.addElement(foreach);

            sb.setLength(0);
            sb.append("when ");
            sb.append(keyColumnName);
            sb.append("=");
            sb.append(keyParam);
            sb.append(" then ");
            if (modifiedTime != null && modifiedTime.equalsIgnoreCase(columnName)) {
                sb.append("now()");
                foreach.addElement(new TextElement(sb.toString()));
            } else {
                XmlElement ifElement = new XmlElement("if");
                ifSb.setLength(0);
                ifSb.append(MyBatis3FormattingUtilities.getBatchParameterClauseOnly(introspectedColumn));
                ifSb.append("!=null");
                ifElement.addAttribute(new Attribute("test", ifSb.toString()));
                foreach.addElement(ifElement);

                sb.append(MyBatis3FormattingUtilities.getBatchParameterClause(introspectedColumn));
                ifElement.addElement(new TextElement(sb.toString()));
            }
        }

        sb.setLength(0);
        sb.append("where ");
        sb.append(keyColumnName);
        sb.append(" in");
        answer.addElement(new TextElement(sb.toString()));
        sb.setLength(0);
        sb.append("<foreach collection=\"list\" item=\"obj\" separator=\",\" open=\"(\" close=\")\">");
        answer.addElement(new TextElement(sb.toString()));
        sb.setLength(0);
        sb.append("    ");
        sb.append(keyParam);
        answer.addElement(new TextElement(sb.toString()));
        sb.setLength(0);
        sb.append("</foreach>");
        answer.addElement(new TextElement(sb.toString()));

        if (context.getPlugins()
                .sqlMapUpdateByPrimaryKeySelectiveElementGenerated(answer,
                        introspectedTable)) {
            parentElement.addElement(answer);
        }
    }
}

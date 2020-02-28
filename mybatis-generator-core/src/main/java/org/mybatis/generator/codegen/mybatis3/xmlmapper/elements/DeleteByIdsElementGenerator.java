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
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;

/**
 * modified
 * DeleteByIds
 * @author xiangwb
 */
public class DeleteByIdsElementGenerator extends
        AbstractXmlElementGenerator {

    private boolean isSimple;

    public DeleteByIdsElementGenerator(boolean isSimple) {
        super();
        this.isSimple = isSimple;
    }

    /**
     * modified
     * deletedByIds
     */
    @Override
    public void addElements(XmlElement parentElement) {
        XmlElement answer = new XmlElement("delete");

        answer.addAttribute(new Attribute(
                "id", introspectedTable.getDeleteByIdsStatementId()));
//        String parameterClass;
//        if (!isSimple && introspectedTable.getRules().generatePrimaryKeyClass()) {
//            parameterClass = introspectedTable.getPrimaryKeyType();
//        } else {
//            // PK fields are in the base class. If more than on PK
//            // field, then they are coming in a map.
//            if (introspectedTable.getPrimaryKeyColumns().size() > 1) {
//                parameterClass = "map"; //$NON-NLS-1$
//            } else {
//                parameterClass = introspectedTable.getPrimaryKeyColumns()
//                        .get(0).getFullyQualifiedJavaType().toString();
//            }
//        }
//        answer.addAttribute(new Attribute("parameterType", //$NON-NLS-1$
//                parameterClass));

        context.getCommentGenerator().addComment(answer);

        StringBuilder sb = new StringBuilder();
        sb.append("delete from ");
//        sb.append(introspectedTable.getFullyQualifiedTableNameAtRuntime());
        answer.addElement(new TextElement(sb.toString()));
        answer.addElement(getTable());

        sb.setLength(0);
        sb.append("where ");
        IntrospectedColumn introspectedColumn = introspectedTable.getPrimaryKeyColumns().get(0);
        String key = MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn);
        sb.append(key);
        sb.append(" in");
        answer.addElement(new TextElement(sb.toString()));

        sb.setLength(0);
        sb.append("<foreach collection=\"list\" item=\"id\" separator=\",\" open=\"(\" close=\")\">");
        answer.addElement(new TextElement(sb.toString()));

        sb.setLength(0);
        sb.append("    #{id}");
        answer.addElement(new TextElement(sb.toString()));

        sb.setLength(0);
        sb.append("</foreach>");
        answer.addElement(new TextElement(sb.toString()));

        if (context.getPlugins()
                .sqlMapDeleteByPrimaryKeyElementGenerated(answer,
                        introspectedTable)) {
            parentElement.addElement(answer);
        }
    }
}

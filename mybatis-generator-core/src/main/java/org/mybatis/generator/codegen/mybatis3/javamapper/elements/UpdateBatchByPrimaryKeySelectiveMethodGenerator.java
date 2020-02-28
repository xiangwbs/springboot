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
package org.mybatis.generator.codegen.mybatis3.javamapper.elements;

import org.mybatis.generator.api.dom.java.*;

import java.util.Set;
import java.util.TreeSet;

/**
 * modified
 * UpdateBatch
 * @author xiangwb
 *
 */
public class UpdateBatchByPrimaryKeySelectiveMethodGenerator extends
        AbstractJavaMapperMethodGenerator {

    public UpdateBatchByPrimaryKeySelectiveMethodGenerator() {
        super();
    }

    /**
     * modified
     * -去除WithBLOBsType
     * @param interfaze
     */
    @Override
    public void addInterfaceElements(Interface interfaze) {
        Set<FullyQualifiedJavaType> importedTypes = new TreeSet<>();
        importedTypes.add(FullyQualifiedJavaType.getNewListInstance());
        FullyQualifiedJavaType recordType;
        if (introspectedTable.getRules().generateRecordWithBLOBsClass()) {
            recordType = new FullyQualifiedJavaType(introspectedTable.getRecordWithBLOBsType());
        } else {
            recordType = new FullyQualifiedJavaType(introspectedTable.getBaseRecordType());
        }
        importedTypes.add(recordType);

        Method method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(FullyQualifiedJavaType.getIntInstance());
        method.setName(introspectedTable.getUpdateBatchByPrimaryKeyStatementId());
        FullyQualifiedJavaType paramType = FullyQualifiedJavaType.getNewListInstance();
        paramType.addTypeArgument(recordType);
        method.addParameter(new Parameter(paramType, "records"));

        context.getCommentGenerator().addGeneralMethodComment(method, introspectedTable);

        addMapperAnnotations(method);

        if (context.getPlugins().clientUpdateByPrimaryKeySelectiveMethodGenerated(method, interfaze, introspectedTable)) {
            addExtraImports(interfaze);
            interfaze.addImportedTypes(importedTypes);
            interfaze.addMethod(method);
        }
    }

    public void addMapperAnnotations(Method method) {
    }

    public void addExtraImports(Interface interfaze) {
    }
}

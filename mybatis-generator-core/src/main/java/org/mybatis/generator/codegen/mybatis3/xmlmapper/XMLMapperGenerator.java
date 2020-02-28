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
package org.mybatis.generator.codegen.mybatis3.xmlmapper;

import static org.mybatis.generator.internal.util.messages.Messages.getString;

import org.mybatis.generator.api.FullyQualifiedTable;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.AbstractXmlGenerator;
import org.mybatis.generator.codegen.XmlConstants;
import org.mybatis.generator.codegen.mybatis3.xmlmapper.elements.*;

/**
 * @author Jeff Butler
 */
public class XMLMapperGenerator extends AbstractXmlGenerator {

    public XMLMapperGenerator() {
        super();
    }

    /**
     * modified
     * 获取mapper.xml信息
     * -去除非必要方法,以及调整方法顺序
     * -添加findAll方法
     * -添加addTableElement
     * -添加addConditionElement
     * -添加deleteByIds方法
     * -添加delete方法
     * -添加findByIds方法
     * -添加find方法
     * -添加insertBatch方法
     * -添加updateBatch方法
     */
    protected XmlElement getSqlMapElement() {
        FullyQualifiedTable table = introspectedTable.getFullyQualifiedTable();
        progressCallback.startTask(getString(
                "Progress.12", table.toString())); //$NON-NLS-1$
        XmlElement answer = new XmlElement("mapper"); //$NON-NLS-1$
        String namespace = introspectedTable.getMyBatis3SqlMapNamespace();
        answer.addAttribute(new Attribute("namespace", //$NON-NLS-1$
                namespace));

        context.getCommentGenerator().addRootComment(answer);
        //去除非必要方法,以及调整方法顺序
        addResultMapWithoutBLOBsElement(answer);//BaseResultMap
        addTableElement(answer);//新增Table
        addBaseColumnListElement(answer);//Base_Column_List
        addConditionElement(answer);//新增Condition
//        addResultMapWithBLOBsElement(answer);
        addExampleWhereClauseElement(answer);
        addMyBatis3UpdateByExampleWhereClauseElement(answer);
//        addBlobColumnListElement(answer);
        addInsertElement(answer);//insert
        addInsertBatchElement(answer);//新增insertBatch
//        addInsertSelectiveElement(answer);
        addDeleteByPrimaryKeyElement(answer);//deleteById
        addDeleteByIdsElement(answer);//新增deleteByIds
        addDeleteElement(answer);//新增delete
        addDeleteByExampleElement(answer);
        addUpdateByPrimaryKeySelectiveElement(answer);//update
        addUpdateBatchByPrimaryKeySelectiveElement(answer);//新增updateBatch
//        addUpdateByPrimaryKeyWithBLOBsElement(answer);
//        addUpdateByPrimaryKeyWithoutBLOBsElement(answer);
        addUpdateByExampleSelectiveElement(answer);
        addUpdateByExampleWithBLOBsElement(answer);
        addUpdateByExampleWithoutBLOBsElement(answer);
        addSelectByPrimaryKeyElement(answer);//findById
        addSelectByIdsElement(answer);//新增findByIds
        addSelectElement(answer);//新增find
        addSelectByExampleWithBLOBsElement(answer);
        addSelectByExampleWithoutBLOBsElement(answer);
        addCountByExampleElement(answer);
        addSelectAllElement(answer);//新增findAll
        return answer;
    }

    /**
     * modified
     * addSelectAllElement
     */
    protected void addSelectAllElement(XmlElement parentElement) {
        AbstractXmlElementGenerator elementGenerator = new SimpleSelectAllElementGenerator();
        initializeAndExecuteGenerator(elementGenerator, parentElement);
    }

    protected void addResultMapWithoutBLOBsElement(XmlElement parentElement) {
        if (introspectedTable.getRules().generateBaseResultMap()) {
            AbstractXmlElementGenerator elementGenerator = new ResultMapWithoutBLOBsElementGenerator(false);
            initializeAndExecuteGenerator(elementGenerator, parentElement);
        }
    }

    protected void addResultMapWithBLOBsElement(XmlElement parentElement) {
        if (introspectedTable.getRules().generateResultMapWithBLOBs()) {
            AbstractXmlElementGenerator elementGenerator = new ResultMapWithBLOBsElementGenerator();
            initializeAndExecuteGenerator(elementGenerator, parentElement);
        }
    }

    protected void addExampleWhereClauseElement(XmlElement parentElement) {
        if (introspectedTable.getRules().generateSQLExampleWhereClause()) {
            AbstractXmlElementGenerator elementGenerator = new ExampleWhereClauseElementGenerator(
                    false);
            initializeAndExecuteGenerator(elementGenerator, parentElement);
        }
    }

    protected void addMyBatis3UpdateByExampleWhereClauseElement(
            XmlElement parentElement) {
        if (introspectedTable.getRules()
                .generateMyBatis3UpdateByExampleWhereClause()) {
            AbstractXmlElementGenerator elementGenerator = new ExampleWhereClauseElementGenerator(
                    true);
            initializeAndExecuteGenerator(elementGenerator, parentElement);
        }
    }

    protected void addBaseColumnListElement(XmlElement parentElement) {
        if (introspectedTable.getRules().generateBaseColumnList()) {
            AbstractXmlElementGenerator elementGenerator = new BaseColumnListElementGenerator();
            initializeAndExecuteGenerator(elementGenerator, parentElement);
        }
    }

    /**
     * modified
     * addConditionElement
     */
    protected void addConditionElement(XmlElement parentElement) {
        if (introspectedTable.getRules().generateCondition()) {
            AbstractXmlElementGenerator elementGenerator = new ConditionElementGenerator();
            initializeAndExecuteGenerator(elementGenerator, parentElement);
        }
    }

    /**
     * modified
     * addTableElement
     */
    protected void addTableElement(XmlElement parentElement) {
        if (introspectedTable.getRules().generateTable()) {
            AbstractXmlElementGenerator elementGenerator = new TableElementGenerator();
            initializeAndExecuteGenerator(elementGenerator, parentElement);
        }
    }

    protected void addBlobColumnListElement(XmlElement parentElement) {
        if (introspectedTable.getRules().generateBlobColumnList()) {
            AbstractXmlElementGenerator elementGenerator = new BlobColumnListElementGenerator();
            initializeAndExecuteGenerator(elementGenerator, parentElement);
        }
    }

    protected void addSelectByExampleWithoutBLOBsElement(
            XmlElement parentElement) {
        if (introspectedTable.getRules().generateSelectByExampleWithoutBLOBs()) {
            AbstractXmlElementGenerator elementGenerator = new SelectByExampleWithoutBLOBsElementGenerator();
            initializeAndExecuteGenerator(elementGenerator, parentElement);
        }
    }

    protected void addSelectByExampleWithBLOBsElement(XmlElement parentElement) {
        if (introspectedTable.getRules().generateSelectByExampleWithBLOBs()) {
            AbstractXmlElementGenerator elementGenerator = new SelectByExampleWithBLOBsElementGenerator();
            initializeAndExecuteGenerator(elementGenerator, parentElement);
        }
    }

    protected void addSelectByPrimaryKeyElement(XmlElement parentElement) {
        if (introspectedTable.getRules().generateSelectByPrimaryKey()) {
            AbstractXmlElementGenerator elementGenerator = new SelectByPrimaryKeyElementGenerator();
            initializeAndExecuteGenerator(elementGenerator, parentElement);
        }
    }

    /**
     * modified
     * addSelectElement
     */
    protected void addSelectElement(XmlElement parentElement) {
        if (introspectedTable.getRules().generateSelect()) {
            AbstractXmlElementGenerator elementGenerator = new SelectElementGenerator();
            initializeAndExecuteGenerator(elementGenerator, parentElement);
        }
    }

    /**
     * modified
     * addSelectByIdsElement
     */
    protected void addSelectByIdsElement(XmlElement parentElement) {
        if (introspectedTable.getRules().generateSelectByIds()) {
            AbstractXmlElementGenerator elementGenerator = new SelectByIdsElementGenerator();
            initializeAndExecuteGenerator(elementGenerator, parentElement);
        }
    }

    protected void addDeleteByExampleElement(XmlElement parentElement) {
        if (introspectedTable.getRules().generateDeleteByExample()) {
            AbstractXmlElementGenerator elementGenerator = new DeleteByExampleElementGenerator();
            initializeAndExecuteGenerator(elementGenerator, parentElement);
        }
    }

    protected void addDeleteByPrimaryKeyElement(XmlElement parentElement) {
        if (introspectedTable.getRules().generateDeleteByPrimaryKey()) {
            AbstractXmlElementGenerator elementGenerator = new DeleteByPrimaryKeyElementGenerator(false);
            initializeAndExecuteGenerator(elementGenerator, parentElement);
        }
    }

    /**
     * modified
     * addDeleteByIdsElement
     */
    protected void addDeleteByIdsElement(XmlElement parentElement) {
        if (introspectedTable.getRules().generateDeleteByIdsKey()) {
            AbstractXmlElementGenerator elementGenerator = new DeleteByIdsElementGenerator(false);
            initializeAndExecuteGenerator(elementGenerator, parentElement);
        }
    }

    /**
     * modified
     * addDeleteElement
     */
    protected void addDeleteElement(XmlElement parentElement) {
        if (introspectedTable.getRules().generateDeleteKey()) {
            AbstractXmlElementGenerator elementGenerator = new DeleteElementGenerator(false);
            initializeAndExecuteGenerator(elementGenerator, parentElement);
        }
    }

    protected void addInsertElement(XmlElement parentElement) {
        if (introspectedTable.getRules().generateInsert()) {
            AbstractXmlElementGenerator elementGenerator = new InsertElementGenerator(false);
            initializeAndExecuteGenerator(elementGenerator, parentElement);
        }
    }

    /**
     * modified
     * addInsertBatchElement
     */
    protected void addInsertBatchElement(XmlElement parentElement) {
        if (introspectedTable.getRules().generateInsert()) {
            AbstractXmlElementGenerator elementGenerator = new InsertBatchElementGenerator(false);
            initializeAndExecuteGenerator(elementGenerator, parentElement);
        }
    }

    protected void addInsertSelectiveElement(XmlElement parentElement) {
        if (introspectedTable.getRules().generateInsertSelective()) {
            AbstractXmlElementGenerator elementGenerator = new InsertSelectiveElementGenerator();
            initializeAndExecuteGenerator(elementGenerator, parentElement);
        }
    }

    protected void addCountByExampleElement(XmlElement parentElement) {
        if (introspectedTable.getRules().generateCountByExample()) {
            AbstractXmlElementGenerator elementGenerator = new CountByExampleElementGenerator();
            initializeAndExecuteGenerator(elementGenerator, parentElement);
        }
    }

    protected void addUpdateByExampleSelectiveElement(XmlElement parentElement) {
        if (introspectedTable.getRules().generateUpdateByExampleSelective()) {
            AbstractXmlElementGenerator elementGenerator = new UpdateByExampleSelectiveElementGenerator();
            initializeAndExecuteGenerator(elementGenerator, parentElement);
        }
    }

    protected void addUpdateByExampleWithBLOBsElement(XmlElement parentElement) {
        if (introspectedTable.getRules().generateUpdateByExampleWithBLOBs()) {
            AbstractXmlElementGenerator elementGenerator = new UpdateByExampleWithBLOBsElementGenerator();
            initializeAndExecuteGenerator(elementGenerator, parentElement);
        }
    }

    protected void addUpdateByExampleWithoutBLOBsElement(
            XmlElement parentElement) {
        if (introspectedTable.getRules().generateUpdateByExampleWithoutBLOBs()) {
            AbstractXmlElementGenerator elementGenerator = new UpdateByExampleWithoutBLOBsElementGenerator();
            initializeAndExecuteGenerator(elementGenerator, parentElement);
        }
    }

    protected void addUpdateByPrimaryKeySelectiveElement(
            XmlElement parentElement) {
        if (introspectedTable.getRules().generateUpdateByPrimaryKeySelective()) {
            AbstractXmlElementGenerator elementGenerator = new UpdateByPrimaryKeySelectiveElementGenerator();
            initializeAndExecuteGenerator(elementGenerator, parentElement);
        }
    }

    /**
     * modified
     * addUpdateBatchByPrimaryKeySelectiveElement
     */
    protected void addUpdateBatchByPrimaryKeySelectiveElement(
            XmlElement parentElement) {
        if (introspectedTable.getRules().generateUpdateBatchByPrimaryKeySelective()) {
            AbstractXmlElementGenerator elementGenerator = new UpdateBatchByPrimaryKeySelectiveElementGenerator();
            initializeAndExecuteGenerator(elementGenerator, parentElement);
        }
    }

    protected void addUpdateByPrimaryKeyWithBLOBsElement(
            XmlElement parentElement) {
        if (introspectedTable.getRules().generateUpdateByPrimaryKeyWithBLOBs()) {
            AbstractXmlElementGenerator elementGenerator = new UpdateByPrimaryKeyWithBLOBsElementGenerator();
            initializeAndExecuteGenerator(elementGenerator, parentElement);
        }
    }

    protected void addUpdateByPrimaryKeyWithoutBLOBsElement(
            XmlElement parentElement) {
        if (introspectedTable.getRules()
                .generateUpdateByPrimaryKeyWithoutBLOBs()) {
            AbstractXmlElementGenerator elementGenerator = new UpdateByPrimaryKeyWithoutBLOBsElementGenerator(false);
            initializeAndExecuteGenerator(elementGenerator, parentElement);
        }
    }

    /**
     * 初始化每个方法的元素信息
     */
    protected void initializeAndExecuteGenerator(
            AbstractXmlElementGenerator elementGenerator,
            XmlElement parentElement) {
        elementGenerator.setContext(context);
        elementGenerator.setIntrospectedTable(introspectedTable);
        elementGenerator.setProgressCallback(progressCallback);
        elementGenerator.setWarnings(warnings);
        //添加每个方法的元素信息
        elementGenerator.addElements(parentElement);
    }

    @Override
    public Document getDocument() {
        Document document = new Document(
                XmlConstants.MYBATIS3_MAPPER_PUBLIC_ID,
                XmlConstants.MYBATIS3_MAPPER_SYSTEM_ID);
        //获取xml元素信息
        document.setRootElement(getSqlMapElement());

        if (!context.getPlugins().sqlMapDocumentGenerated(document,
                introspectedTable)) {
            document = null;
        }

        return document;
    }
}

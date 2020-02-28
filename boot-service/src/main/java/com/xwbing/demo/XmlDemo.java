package com.xwbing.demo;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

/**
 * 创建日期: 2017年2月16日 下午1:23:16
 * 作者: xiangwb
 */

public class XmlDemo {

    /**
     * 功能描述：将doc对象（xml）写到文件中
     * 作 者：xwb
     * 创建时间：2017年2月16日 下午2:59:26
     *
     * @param doc
     * @param xml
     * @throws Exception
     */
    private static void write2xml(Document doc, String fos) throws Exception {
        FileOutputStream out = new FileOutputStream(fos);
        // dom4j 提供类格式工具，可以输出时候对doc进行化妆，搞漂亮一些
        OutputFormat fmt = OutputFormat.createPrettyPrint();
        XMLWriter writer = new XMLWriter(out, fmt);// 格式
        writer.write(doc);
        writer.close();
    }

    /**
     * 功能描述： 读取XML文件到doc对象
     * 作 者：xwb
     * 创建时间：2017年2月16日 下午3:01:33
     *
     * @return
     * @throws Exception
     */
    private static Document readXml(String fis) throws Exception {
        SAXReader reader = new SAXReader();
        FileInputStream in = new FileInputStream(fis);
        Document doc = reader.read(in);
        return doc;
    }

    public static void main(String[] args) throws Exception {
        Document doc = readXml("xmlDemo.xml");
        System.out.println(doc.asXML() + "...............");

        // doc 是全部的XML文件的内容
        // doc 引用的是内存中的对象。
        Element root = doc.getRootElement();// 获取xml的根元素,访问xml中数据的唯一入口
        // 获取根元素下所有子元素，并遍历
        List<Element> list = root.elements();
        for (Element e2 : list) {
            System.out.println(e2.getName());// 获取元素名字
            System.out.println(e2.getText());// 获取元素内文本内容
        }

        Element book = root.element("book");// 获取满足第一个条件的子元素
        Element name = book.element("name");
        name.setText("轶名名");// 修改元素内容
        Attribute id = book.attribute("id");// 获取属性
        System.out.println(id.getValue());
        id.setValue("b3");// 修改属性

        // 如果修改了doc 就能实现修改xml并写文件
        // 为根元素添加子元素
        Element newOne = root.addElement("book8");
        newOne.addElement("name").setText("18岁给我一姑娘");
        newOne.addAttribute("id", "b5");
        newOne.addAttribute("lang", "中文");

        // 找到元素并且删除这个子元素
        Element book1 = root.element("book8");
        root.remove(book1);
        write2xml(doc, "demo.xml");

        // 创建新的xml document对象
        Document newDoc = DocumentHelper.createDocument();
        // 为doc添加根元素
        Element newRoot = newDoc.addElement("students");
        newRoot.addElement("stu1");
        newRoot.addElement("stu2");
        write2xml(newDoc, "xmlDemoStu.xml");
    }
}

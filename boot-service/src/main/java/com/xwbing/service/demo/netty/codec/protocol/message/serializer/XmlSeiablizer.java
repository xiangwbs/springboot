package com.xwbing.service.demo.netty.codec.protocol.message.serializer;

import java.nio.charset.StandardCharsets;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class XmlSeiablizer implements ISerializable {
    @Override
    public <T> T deserializable(Class<T> clazz, byte[] bytes) {
        XStream xStream = new XStream(new DomDriver());
        XStream.setupDefaultSecurity(xStream);
        xStream.allowTypesByWildcard(new String[] { "com.xwbing.service.demo.netty.codec.protocol.message.**" });
        String xml = new String(bytes, StandardCharsets.UTF_8);
        return (T)xStream.fromXML(xml);
    }

    @Override
    public <T> byte[] serializable(T object) {
        XStream xStream = new XStream(new DomDriver());
        XStream.setupDefaultSecurity(xStream);
        xStream.allowTypesByWildcard(new String[] { "com.xwbing.service.demo.netty.codec.protocol.message.**" });
        return xStream.toXML(object).getBytes(StandardCharsets.UTF_8);
    }
}

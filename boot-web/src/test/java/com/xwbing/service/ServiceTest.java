package com.xwbing.service;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xwbing.BaseTest;
import com.xwbing.domain.entity.model.ExpressInfo;
import com.xwbing.domain.entity.sys.DataDictionary;
import com.xwbing.domain.entity.sys.SysRole;
import com.xwbing.domain.entity.sys.SysUserLoginInOut;
import com.xwbing.domain.entity.vo.ExpressInfoVo;
import com.xwbing.domain.mapper.sys.SysUserLoginInOutMapper;
import com.xwbing.rabbit.Sender;
import com.xwbing.redis.RedisService;
import com.xwbing.service.rest.ExpressDeliveryService;
import com.xwbing.service.rest.QRCodeZipService;
import com.xwbing.service.sys.DataDictionaryService;
import com.xwbing.service.sys.SysRoleService;
import com.xwbing.util.Pagination;
import com.xwbing.util.RSAUtil;
import com.xwbing.util.RestMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.internal.DefaultShellCallback;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 项目名称: boot-module-pro
 * 创建时间: 2018/6/1 13:20
 * 作者: xiangwb
 * 说明: 服务层功能测试
 */
@Slf4j
public class ServiceTest extends BaseTest {
    @Resource
    private RedisService redisService;
    @Resource
    private ExpressDeliveryService expressDeliveryService;
    @Resource
    private QRCodeZipService qrCodeZipService;
    @Resource
    private Sender sender;
    @Resource
    private SysRoleService sysRoleService;
    @Resource
    private DataDictionaryService dictionaryService;
    @Resource
    private SysUserLoginInOutMapper loginInOutMapper;

    @Test
    public void page() {
        log.info("分页功能测试");
        Map<String, Object> map = new HashMap<>();
        map.put("inout", 1);
        Page<SysUserLoginInOut> page = PageHelper.startPage(1, 10).doSelectPage(() -> loginInOutMapper.findByInoutType(map));
        PageInfo<SysUserLoginInOut> pageInfo = PageHelper.startPage(1, 10).doSelectPageInfo(() -> loginInOutMapper.findByInoutType(map));
        Pagination page1 = new Pagination(pageInfo);
        Pagination page2 = new Pagination(page);
        long count = PageHelper.count(() -> loginInOutMapper.findByInoutType(map));
    }

    @Transactional("jpaTransactionManager")
    @Test
    public void jpaTransactionTest() {
        log.info("jpa数据源事务回滚");
        DataDictionary dictionary = new DataDictionary();
        dictionary.setDescription("serviceTest");
        dictionary.setEnable("Y");
        dictionary.setCode("serviceTest");
        dictionary.setName("serviceTest");
        dictionary.setParentId("root");
        RestMessage save = dictionaryService.save(dictionary);
        Assert.assertTrue(save.isSuccess());
    }

    @Transactional
    @Test
    public void mybatisTransactionTest() {
        log.info("mybatis数据源事务回滚");
        SysRole sysRole = new SysRole();
        sysRole.setName("serviceTest");
        sysRole.setCode("serviceTest");
        sysRole.setEnable("Y");
        sysRole.setRemark("serviceTest");
        RestMessage save = sysRoleService.save(sysRole);
        Assert.assertTrue(save.isSuccess());
    }

    @Test
    public void listShipperCodeTest() {
        log.debug("获取快递列表");
        List<JSONObject> list = expressDeliveryService.listShipperCode();
        Assert.assertSame(12, list.size());
    }

    @Test
    public void expressInfoTest() {
        log.debug("快递查询");
        ExpressInfo info = new ExpressInfo();
        info.setLogisticCode("800430151641580299");
        info.setShipperCode("YTO");
        if (StringUtils.isEmpty(info.getLogisticCode()) || StringUtils.isEmpty(info.getShipperCode())) {
            log.error("快递公司或物流单号不能为空");
        }
        ExpressInfoVo infoVo = expressDeliveryService.queryOrderTraces(info);
        Assert.assertTrue(infoVo.isSuccess());
    }

    @Test
    public void createQRCodeTest() {
        RestMessage qrCode = qrCodeZipService.createQRCode("test", "test");
        Assert.assertTrue(qrCode.isSuccess());
    }

    @Test
    public void decodeQRCodeTest() throws IOException {
        ClassPathResource pic = new ClassPathResource("pic");
        String path = pic.getFile().getAbsolutePath();
        File file = new File(path + File.separator + "QRCode.png");
        RestMessage decode = qrCodeZipService.decode(file);
        Assert.assertTrue(decode.isSuccess());
    }

    @Test
    public void rsaTest() {
        String en = RSAUtil.encrypt("123456");
        String de = RSAUtil.decrypt(en);
        Assert.assertEquals("123456", de);
    }

    @Test
    public void sendTest() {
        sender.sendMessage(new String[]{"mq测试"});
    }

    @Test
    public void redisTest() {
        redisService.set("redis", "xwbing");
        String s = redisService.get("redis");
        Assert.assertEquals("xwbing", s);
    }

    @Test
    public void mybatisGeneratorTest() throws Exception {
        List<String> warnings = new ArrayList<>();
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream is = classloader.getResourceAsStream("generatorConfig.xml");
        ConfigurationParser cp = new ConfigurationParser(warnings);
        Configuration config = cp.parseConfiguration(is);
        DefaultShellCallback callback = new DefaultShellCallback(false);
        MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, callback, warnings);
        myBatisGenerator.generate(null);
    }
}

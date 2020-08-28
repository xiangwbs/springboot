package com.xwbing.web.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.internal.DefaultShellCallback;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xwbing.starter.clusterseq.ClusterSeqGenerator;
import com.xwbing.starter.redis.RedisService;
import com.xwbing.service.domain.entity.model.ExpressInfo;
import com.xwbing.service.domain.entity.sys.DataDictionary;
import com.xwbing.service.domain.entity.sys.SysRole;
import com.xwbing.service.domain.entity.sys.SysUserLoginInOut;
import com.xwbing.service.domain.entity.vo.ExpressInfoVo;
import com.xwbing.service.domain.mapper.sys.SysUserLoginInOutMapper;
import com.xwbing.service.enums.KdniaoShipperCodeEnum;
import com.xwbing.service.rabbit.Sender;
import com.xwbing.service.service.rest.ExpressDeliveryService;
import com.xwbing.service.service.rest.QRCodeZipService;
import com.xwbing.service.service.sys.DataDictionaryService;
import com.xwbing.service.service.sys.SysRoleService;
import com.xwbing.service.util.Pagination;
import com.xwbing.service.util.RSAUtil;
import com.xwbing.service.util.RestMessage;
import com.xwbing.web.BaseTest;

import lombok.extern.slf4j.Slf4j;

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
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private ClusterSeqGenerator clusterSeqGenerator;

    @Test
    public void page() {
        log.info("分页功能测试");
        Map<String, Object> map = new HashMap<>();
        map.put("inout", 1);
        Page<SysUserLoginInOut> page = PageHelper.startPage(1, 10)
                .doSelectPage(() -> loginInOutMapper.findByInoutType(map));
        PageInfo<SysUserLoginInOut> pageInfo = PageHelper.startPage(1, 10)
                .doSelectPageInfo(() -> loginInOutMapper.findByInoutType(map));
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
        Assertions.assertTrue(save.isSuccess());
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
        Assertions.assertTrue(save.isSuccess());
    }

    @Test
    public void listShipperCodeTest() {
        log.debug("获取快递列表");
        List<JSONObject> list = KdniaoShipperCodeEnum.list();
        Assertions.assertSame(12, list.size());
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
        Assertions.assertTrue(infoVo.isSuccess());
    }

    @Test
    public void createQRCodeTest() {
        log.info("生成二维码");
        RestMessage qrCode = qrCodeZipService.createQRCode("test", "test");
        Assertions.assertTrue(qrCode.isSuccess());
    }

    @Test
    public void decodeQRCodeTest() throws IOException {
        log.info("解析二维码");
        ClassPathResource pic = new ClassPathResource("pic");
        String path = pic.getFile().getAbsolutePath();
        File file = new File(path + File.separator + "QRCode.png");
        RestMessage decode = qrCodeZipService.decode(file);
        Assertions.assertTrue(decode.isSuccess());
    }

    @Test
    public void rsaTest() {
        String en = RSAUtil.encrypt("123456");
        String de = RSAUtil.decrypt(en);
        Assertions.assertEquals("123456", de);
    }

    @Test
    public void sendTest() {
        sender.sendMessage(new String[] { "mq测试" });
    }

    @Test
    public void redisTest() {
        redisService.set("redis", "xwbing");
        String s = redisService.get("redis");
        redisTemplate.opsForValue().set("redisTemplate", "redisTemplate");
        Assertions.assertEquals("xwbing", s);
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

    @Test
    public void getSeqId() {
        log.info("获取全局序列号");
        Long id = clusterSeqGenerator.getSeqId("test");
        log.info("getSeqId:{}", id);
    }
}
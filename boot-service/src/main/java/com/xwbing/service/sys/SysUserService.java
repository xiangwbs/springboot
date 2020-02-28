package com.xwbing.service.sys;

import com.alibaba.fastjson.JSONObject;
import com.xwbing.constant.CommonConstant;
import com.xwbing.constant.CommonEnum;
import com.xwbing.domain.entity.dto.UserDto;
import com.xwbing.domain.entity.model.EmailModel;
import com.xwbing.domain.entity.sys.*;
import com.xwbing.domain.repository.sys.SysUserRepository;
import com.xwbing.exception.BusinessException;
import com.xwbing.util.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 说明: 用户服务层
 * 项目名称: boot-module-demo
 * 创建时间: 2017/5/5 16:44
 * 作者:  xiangwb
 */
@Service
public class SysUserService {
    @Resource
    private SysUserRepository sysUserRepository;
    @Resource
    private SysConfigService sysConfigService;
    @Resource
    private SysRoleService sysRoleService;
    @Resource
    private SysAuthorityService sysAuthorityService;
    @Resource
    private SysUserRoleService sysUserRoleService;

    /**
     * 增
     *
     * @param sysUser
     * @return
     */
    public RestMessage save(SysUser sysUser) {
        RestMessage result = new RestMessage();
        //检查用户名是否存在
        SysUser old = getByUserName(sysUser.getUserName());
        if (old != null) {
            throw new BusinessException("已经存在此用户名");
        }
        String id = PassWordUtil.createId();
        sysUser.setId(id);
        sysUser.setCreateTime(new Date());
        // 获取初始密码
        String[] res = PassWordUtil.getUserSecret(null, null);
        sysUser.setSalt(res[1]);
        sysUser.setPassword(res[2]);
        // 设置为非管理员
        sysUser.setIsAdmin(CommonEnum.YesOrNoEnum.NO.getCode());
        SysUser one = sysUserRepository.save(sysUser);
        if (one == null) {
            throw new BusinessException("新增用户失败");
        }
        //发送邮件
        boolean send = sendEmail(sysUser, res[0]);
        // 发送邮件结束
        if (!send) {
            throw new BusinessException("发送密码邮件错误");
        }
        result.setSuccess(true);
        result.setId(id);
        return result;
    }

    /**
     * 删
     *
     * @param id
     * @return
     */
    public RestMessage removeById(String id) {
        RestMessage result = new RestMessage();
        //检查该用户是否存在
        SysUser old = getById(id);
        if (old == null) {
            throw new BusinessException("该用户不存在");
        }
        SysUser currentInfo = getCurrentInfo();
        if (currentInfo != null) {
            if (id.equals(currentInfo.getId())) {
                throw new BusinessException("不能删除当前登录用户");
            }
            if (CommonEnum.YesOrNoEnum.YES.getCode().equals(old.getIsAdmin())) {
                throw new BusinessException("不能对管理员进行删除操作");
            }
            //删除用户
            sysUserRepository.delete(id);
            //删除用户角色
            List<SysUserRole> sysUserRoles = sysUserRoleService.listByUserId(id);
            if (CollectionUtils.isNotEmpty(sysUserRoles)) {
                sysUserRoleService.removeBatch(sysUserRoles);
            }
            result.setMessage("删除成功");
            result.setSuccess(true);
            return result;
        } else {
            throw new BusinessException("未能获取当前登录用户信息");
        }
    }

    /**
     * 更新
     *
     * @param sysUser
     * @return
     */
    public RestMessage update(SysUser sysUser) {
        RestMessage result = new RestMessage();
        //检查该用户是否存在
        SysUser old = getById(sysUser.getId());
        if (old == null) {
            throw new BusinessException("该用户不存在");
        }
        if (CommonEnum.YesOrNoEnum.YES.getCode().equals(old.getIsAdmin())) {
            throw new BusinessException("不能对管理员进行修改操作");
        }
        // 根据实际情况补充
        old.setName(sysUser.getName());
        old.setMail(sysUser.getMail());
        old.setSex(sysUser.getSex());
        old.setModifiedTime(new Date());
//        old.setUserName(sysUser.getUserName());//用户名不能修改
        SysUser one = sysUserRepository.save(old);
        if (one != null) {
            result.setMessage("更新成功");
            result.setId(sysUser.getId());
            result.setSuccess(true);
        } else {
            result.setMessage("更新失败");
        }
        return result;
    }

    /**
     * 单个查询
     *
     * @param id
     * @return
     */
    public SysUser getById(String id) {
        return sysUserRepository.findOne(id);
    }

    /**
     * 根据用户名查找用户
     *
     * @param userName
     * @return
     */
    public SysUser getByUserName(String userName) {
        return sysUserRepository.getByUserName(userName);
    }

    /**
     * 获取用户对象
     *
     * @return
     */
    public SysUser getCurrentInfo() {
        Subject subject = SecurityUtils.getSubject();
        if (subject != null && subject.getPrincipal() != null) {
            return (SysUser) subject.getPrincipal();
        } else {
            return null;
        }
    }

    /**
     * 列表查询
     *
     * @return
     */
    public List<SysUser> listAll() {
        List<SysUser> all = sysUserRepository.findAll();
        if (CollectionUtils.isNotEmpty(all)) {
            all.forEach(sysUser -> {
                CommonEnum.SexEnum sexEnum = Arrays.stream(CommonEnum.SexEnum.values()).filter(obj -> obj.getCode().equals(sysUser.getSex())).findFirst().get();
                sysUser.setSexName(sexEnum.getName());
                sysUser.setCreate(DateUtil2.dateToStr(sysUser.getCreateTime(), DateUtil2.YYYY_MM_DD_HH_MM_SS));
                Date modifiedTime = sysUser.getModifiedTime();
                if (modifiedTime != null) {
                    sysUser.setModified(DateUtil2.dateToStr(sysUser.getModifiedTime(), DateUtil2.YYYY_MM_DD_HH_MM_SS));
                }
            });
        }
        return all;
    }

    /**
     * 重置密码
     *
     * @param id
     * @return
     */
    public RestMessage resetPassWord(String id) {
        RestMessage result = new RestMessage();
        SysUser old = getById(id);
        if (old == null) {
            throw new BusinessException("未查询到用户信息");
        }
        SysUser currentInfo = getCurrentInfo();
        if (currentInfo != null) {
            if (id.equals(currentInfo.getId())) {
                throw new BusinessException("不能重置当前登录用户");
            }
            if (CommonEnum.YesOrNoEnum.YES.getCode().equals(old.getIsAdmin())) {
                throw new BusinessException("管理员密码不能重置");
            }
            //生成密码
            String[] str = PassWordUtil.getUserSecret(null, null);
            old.setSalt(str[1]);
            old.setPassword(str[2]);
            old.setModifiedTime(new Date());
            SysUser save = sysUserRepository.save(old);
            if (save == null) {
                throw new BusinessException("重置密码失败");
            }
            boolean send = sendEmail(old, str[0]);
            if (!send) {
                throw new BusinessException("发送密码邮件错误");
            }
            result.setSuccess(true);
            result.setMessage("重置密码成功");
            return result;
        } else {
            throw new BusinessException("未能获取当前登录用户信息");
        }
    }

    /**
     * 修改密码
     *
     * @param newPassWord
     * @param oldPassWord
     * @return
     */
    public RestMessage updatePassWord(String newPassWord, String oldPassWord, String id) {
        RestMessage result = new RestMessage();
        SysUser sysUser = getById(id);
        if (sysUser == null) {
            throw new BusinessException("该用户不存在");
        }
        boolean flag = checkPassWord(oldPassWord, sysUser.getPassword(), sysUser.getSalt());
        if (!flag) {
            throw new BusinessException("原密码错误,请重新输入");
        }
        //生成密码
        String[] str = PassWordUtil.getUserSecret(newPassWord, null);
        sysUser.setSalt(str[1]);
        sysUser.setPassword(str[2]);
        sysUser.setModifiedTime(new Date());
        SysUser save = sysUserRepository.save(sysUser);
        if (save != null) {
            result.setMessage("修改密码成功");
            result.setSuccess(true);
        } else {
            result.setMessage("修改密码失败");
        }
        return result;
    }

    /**
     * 根据用户主键，是否有效，查找所拥有的权限
     *
     * @param userId
     * @param enable
     * @return
     */
    public List<SysAuthority> listAuthorityByIdAndEnable(String userId, String enable) {
        List<SysAuthority> list = new ArrayList<>();
        //根据用戶id和是否启用获取获取角色
        List<SysRole> sysRoles = sysRoleService.listByUserIdEnable(userId, enable);
        if (CollectionUtils.isEmpty(sysRoles)) {
            return list;
        }
        //遍历获取每个角色拥有的权限，并去重
        List<SysAuthority> temp;
        for (SysRole sysRole : sysRoles) {
            temp = sysAuthorityService.listByRoleIdEnable(sysRole.getId(), enable);
            if (CollectionUtils.isNotEmpty(temp)) {
                for (SysAuthority auth : temp) {
                    if (list.contains(auth)) {
                        continue;// 如果存在，那么去除
                    }
                    list.add(auth);
                }
            }
        }
        return list;
    }

    /**
     * 导出excel
     */
    public void exportReport(HttpServletResponse response) {
        String fileName = CommonConstant.USER_REPORT_FILE_NAME;//文件名
        try {
            ServletOutputStream outputStream = response.getOutputStream();
            BufferedOutputStream bufferedOutPut = new BufferedOutputStream(outputStream);
            fileName = new String(fileName.getBytes("GBK"), "ISO8859-1");
            response.reset();
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);// 指定下载的文件名
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0);
            List<UserDto> listDto = listReport();//内容list
            if (CollectionUtils.isEmpty(listDto)) {
                return;
            }
            List<String[]> list = ExcelUtil.convert2List(listDto);
            String title = CommonConstant.USER_REPORT_FILE_NAME;
            String[] columns = CommonConstant.USER_REPORT_COLUMNS;
            bufferedOutPut.flush();
            HSSFWorkbook wb = ExcelUtil.Export(title, columns, list);
            wb.write(bufferedOutPut);
            bufferedOutPut.close();
        } catch (Exception e) {
            throw new BusinessException("导出excel错误");
        }
    }

    /**
     * 校验密码
     *
     * @param sysPassWord 数据库密码
     * @param passWord    明文密码
     * @param salt        盐值
     * @return
     */
    public boolean checkPassWord(String passWord, String sysPassWord, String salt) {
        //根据盐值和明文密码进行sha1散列
        byte[] saltByte = EncodeUtils.hexDecode(salt);
        byte[] hashPassword = Digests.sha1(passWord.getBytes(), saltByte, PassWordUtil.HASH_INTERATIONS);
        String validatePassWord = EncodeUtils.hexEncode(hashPassword);
        //比较编码后的密码和数据库的密码是否相等
        return sysPassWord.equals(validatePassWord);
    }

    /**
     * 获取excel导出列表所需数据
     *
     * @return
     */
    private List<UserDto> listReport() {
        List<UserDto> listDto = new ArrayList<>();
        List<SysUser> list = sysUserRepository.findAll();
        if (CollectionUtils.isNotEmpty(list)) {
            for (SysUser info : list) {
                UserDto temp = new UserDto();
                temp.setIsAdmin(CommonEnum.YesOrNoEnum.YES.getCode().equals(info.getIsAdmin()) ? "是" : "否");
                temp.setMail(info.getMail());
                temp.setName(info.getName());
                temp.setSex(Integer.valueOf(info.getSex()) == 1 ? "男" : "女");
                temp.setUserName(info.getUserName());
                listDto.add(temp);
            }
        }
        return listDto;
    }

    /**
     * 发送邮件
     *
     * @param sysUser
     * @param passWord
     * @return
     */
    private boolean sendEmail(SysUser sysUser, String passWord) {
        SysConfig sysConfig = sysConfigService.getByCode(CommonConstant.EMAIL_KEY);
        if (sysConfig == null) {
            throw new BusinessException("没有查找到邮件配置项");
        }
        JSONObject jsonObject = JSONObject.parseObject(sysConfig.getValue());
        EmailModel emailModel = JSONObject.toJavaObject(jsonObject, EmailModel.class);
        emailModel.setToEmail(sysUser.getMail());
        emailModel.setSubject(emailModel.getSubject());
        emailModel.setCentent(emailModel.getCentent() + " 你的用户名是:" + sysUser.getUserName() + ",密码是:" + passWord);
        return EmailUtil.sendTextEmail(emailModel);
    }
}

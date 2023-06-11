package com.atguigu.auth.controller;

import com.atguigu.auth.service.SysUserService;
import com.atguigu.common.config.exception.GuiguException;
import com.atguigu.common.jwt.JwtHelper;
import com.atguigu.common.result.Result;
import com.atguigu.common.utils.MD5;
import com.atguigu.model.system.SysUser;
import com.atguigu.vo.system.LoginVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.tomcat.util.security.MD5Encoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 后台登录登出
 * </p>
 */
@Api(tags = "后台登录管理")
@RestController
@RequestMapping("/admin/system/index")
public class IndexController {

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private BCryptPasswordEncoder encoder;

    /**
     * 登录
     *
     * @return
     */
    @ApiOperation(value = "登录")
    @PostMapping("/login")
    public Result<?> login(@RequestBody LoginVo loginVo) {
        SysUser sysUser = sysUserService.getByUsername(loginVo.getUsername());
        if (null == sysUser) {
            throw new GuiguException(201, "用户不存在");
        }

        if (!encoder.matches(sysUser.getPassword(), loginVo.getPassword())) {
            throw new GuiguException(201, "密码错误");
        }
        if (sysUser.getStatus().intValue() == 0) {
            throw new GuiguException(201, "用户被禁用");
        }

        Map<String, Object> map = new HashMap<>();
        map.put("token", JwtHelper.createToken(sysUser.getId(), sysUser.getUsername()));
        return Result.ok(map);
    }

    /**
     * 获取用户信息
     * todo 没有token 会怎么样？
     * todo vue 把token放在header里面
     *
     * @return
     */
    @ApiOperation(value = "获取用户信息")
    @GetMapping("/info")
    public Result<?> info(@RequestParam("token") String token) {
        String username = JwtHelper.getUsername(token);
        Map<String, Object> map = sysUserService.getUserInfo(username);
        return Result.ok(map);
    }

    /**
     * 退出
     *
     * @return
     */
    @PostMapping("/logout")
    public Result<?> logout() {
        return Result.ok();
    }


}

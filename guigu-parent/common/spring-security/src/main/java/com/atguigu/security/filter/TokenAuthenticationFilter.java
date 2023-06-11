package com.atguigu.security.filter;

import com.atguigu.common.jwt.JwtHelper;
import com.atguigu.common.result.Result;
import com.atguigu.common.result.ResultCodeEnum;
import com.atguigu.common.utils.ResponseUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 认证解析token过滤器
 * </p>
 */
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private StringRedisTemplate redisTemplate;

    public TokenAuthenticationFilter(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        logger.info("uri:" + request.getRequestURI());
        //如果是登录接口，直接放行
        // todo config配置和这里放行为什么会有两个？
        if ("/admin/system/index/login".equals(request.getRequestURI())) {
            chain.doFilter(request, response);
            return;
        }

        UsernamePasswordAuthenticationToken authentication = getAuthentication(request);
        if (null != authentication) {
            SecurityContextHolder.getContext().setAuthentication(authentication);
            chain.doFilter(request, response);
        } else {
            ResponseUtil.out(response, Result.build(null, ResultCodeEnum.PERMISSION));
        }
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) throws JsonProcessingException {
        // token置于header里
        String token = request.getHeader("token");
        logger.info("token:" + token);
        if (!StringUtils.isEmpty(token)) {
            String useruame = JwtHelper.getUsername(token);
            logger.info("useruame:" + useruame);
            if (!StringUtils.isEmpty(useruame)) {
                String json = redisTemplate.opsForValue().get(useruame);
                if (json == null) {
                    return new UsernamePasswordAuthenticationToken(useruame, null, Collections.emptyList());
                }
                List<String> grantedAuthorities = new ObjectMapper().readValue(json, new TypeReference<List<String>>() {
                });
                List<SimpleGrantedAuthority> collect = grantedAuthorities.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
                return new UsernamePasswordAuthenticationToken(useruame, null, collect);
            }
        }
        return null;
    }
}

package com.example.demo.config;

import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.wall.WallConfig;
import com.alibaba.druid.wall.WallFilter;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by cjyfff on 17-7-2.
 */
@Configuration
@MapperScan(basePackages = "com.cjyfffblog.mapper")
public class MyBatisConfig {

    @Value("${druidOption.setTestWhileIdle}")
    boolean testWhileIdle;

    @Value("${druidOption.setMaxWait}")
    int maxWait;

    @Value("${druidOption.setMinIdle}")
    int minIdle;

    @Value("${druidOption.setMaxActive}")
    int maxActive;

    @Value("${druidOption.setPoolPreparedStatements}")
    boolean poolPreparedStatements;

    @Value("${druidOption.setMaxPoolPreparedStatementPerConnectionSize}")
    int maxPoolPreparedStatementPerConnectionSize;

    @Value("${druidOption.setMaxOpenPreparedStatements}")
    int maxOpenPreparedStatements;

    @Value("${druidOption.dataSourceFilters}")
    String dataSourceFilters;

    @Value("${druidOption.setMultiStatementAllow}")
    private boolean multiStatementAllow;

    @Bean(initMethod = "init", destroyMethod = "close")
    public DataSource druidDataSource(
            @Value("${jdbc.driverClassName}") String driver,
            @Value("${jdbc.url}") String url,
            @Value("${jdbc.username}") String username,
            @Value("${jdbc.password}") String password) {

        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setDriverClassName(driver);
        druidDataSource.setUrl(url);
        druidDataSource.setTestWhileIdle(testWhileIdle);
        druidDataSource.setMaxWait(maxWait);
        druidDataSource.setMinIdle(minIdle);
        druidDataSource.setMaxActive(maxActive);
        druidDataSource.setPoolPreparedStatements(poolPreparedStatements);
        druidDataSource.setMaxPoolPreparedStatementPerConnectionSize(maxPoolPreparedStatementPerConnectionSize);
        druidDataSource.setMaxOpenPreparedStatements(maxOpenPreparedStatements);
        druidDataSource.setUsername(username);
        druidDataSource.setPassword(password);
        List<Filter> proxyFilters = new ArrayList<>();
        WallConfig wallConfig = new WallConfig();
        wallConfig.setMultiStatementAllow(multiStatementAllow);
        WallFilter wallFilter = new WallFilter();
        wallFilter.setConfig(wallConfig);
        proxyFilters.add(wallFilter);
        druidDataSource.setProxyFilters(proxyFilters);
        try {
            druidDataSource.setFilters(dataSourceFilters);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return druidDataSource;
    }

    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        factoryBean.setDataSource(dataSource);

        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        factoryBean.setConfigLocation(resolver.getResource("classpath:mybatis-config.xml"));
//        下边两设置用于*.xml文件，其中setTypeAliasesPackage用于mapper xml中的resultType映射，
//        setMapperLocations用于指定mapper xml的路径
        factoryBean.setTypeAliasesPackage("com.cjyfffblog.domain");
        factoryBean.setMapperLocations(resolver.getResources("classpath*:mybatis-mappers/*.xml"));
        return factoryBean.getObject();
    }
}

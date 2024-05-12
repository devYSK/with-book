package main;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@ComponentScan(basePackages = {"aspects", "controllers", "main", "services"})
@EnableAspectJAutoProxy
public class ProjectConfig {
}

### install

```xml

<plugin>
    <groupId>com.ysar</groupId>
    <artifactId>api-generator-maven-plugin</artifactId>
    <version><!-- 替换为上方版本号 --></version>
    <executions>
        <execution>
            <phase>compile</phase>
            <goals>
                <goal>api-generator</goal>
            </goals>
        </execution>
    </executions>
    <configuration>
        <!-- options in there -->
    </configuration>
</plugin>
```

### options

1. production 输出文件夹，默认为 apiggs
1. source 源码目录
1. dependency 源码依赖的代码目录，以逗号隔开
1. jar 源码依赖的jar包目录，以逗号隔开
1. ignore 忽略某些类型
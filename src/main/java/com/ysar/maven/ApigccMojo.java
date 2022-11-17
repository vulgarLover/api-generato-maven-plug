package com.ysar.maven;

import com.alibaba.fastjson.JSON;
import com.ysar.core.Apigcc;
import com.ysar.core.Context;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * generate rest doc with apigcc
 */
@Mojo(name = "apigcc")
public class ApigccMojo extends AbstractMojo {

    MavenProject project;

    @Parameter
    String id;
    @Parameter
    String name;
    @Parameter
    String description;
    @Parameter
    String build;
    //传字符串，使用逗号分隔
    @Parameter
    String source;
    @Parameter
    String dependency;
    @Parameter
    String jar;
    // 要上传的服务类型，暂时只支持 yapi
    @Parameter
    String targetServer;
    // 上传目标服务的接口地址
    @Parameter
    String serverUrl;
    // 针对于yapi中的项目的token
    @Parameter
    String projectToken;

    @Override
    public void execute() {
        if (getPluginContext().containsKey("project") && getPluginContext().get("project") instanceof MavenProject) {
            project = (MavenProject) getPluginContext().get("project");
            build();
        }
    }

    private void build() {
        Context context = new Context();
        if (source != null) {
            for (String dir : source.split(",")) {
                context.addSource(abs(dir));
            }
        } else {
            context.addSource(project.getBasedir().toPath());
        }
        if (dependency != null) {
            for (String dir : dependency.split(",")) {
                context.addDependency(abs(dir));
            }
        } else {
            MavenProject parent = findParent(project);
            context.addDependency(parent.getBasedir().toPath());
        }
        if (jar != null) {
            for (String dir : jar.split(",")) {
                context.addJar(abs(dir));
            }
        }
//        context.setId(id != null ? id : project.getName());
        if (build != null) {
            context.setBuildPath(abs(build));
        } else {
            context.setBuildPath(Paths.get(project.getBuild().getDirectory()));
        }
        if (name != null) {
            context.setName(name);
        } else {
            context.setName(project.getName());
        }
        if (description != null) {
            context.setDescription(description);
        } else if (project.getDescription() != null) {
            context.setDescription(project.getDescription());
        }
        if (targetServer == null) {
            throw new RuntimeException("请配置目标应用: targetServer");
        }
        if (serverUrl == null) {
            throw new RuntimeException("目标服务url不能为空 serverUrl");
        }
        if (serverUrl.endsWith("/")) {
            serverUrl = serverUrl.substring(0, serverUrl.length() - 1);
        }
        context.setServerUrl(serverUrl);
        context.setTargetServer(targetServer);
        context.setProjectToken(projectToken);

        System.out.println(JSON.toJSONString(context));
        Apigcc apigcc = new Apigcc(context);
        apigcc.parse();
        apigcc.upload();
    }

    private MavenProject findParent(MavenProject mp) {
        if (mp.getParentFile() != null && mp.getParentFile().exists()) {
            return findParent(mp.getParent());
        }
        return mp;
    }

    private Path abs(String dir) {
        Path path = Paths.get(dir);
        if (path.isAbsolute()) {
            return path;
        } else {
            return project.getBasedir().toPath().resolve(path);
        }
    }

    public MavenProject getProject() {
        return project;
    }

    public void setProject(MavenProject project) {
        this.project = project;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBuild() {
        return build;
    }

    public void setBuild(String build) {
        this.build = build;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDependency() {
        return dependency;
    }

    public void setDependency(String dependency) {
        this.dependency = dependency;
    }

    public String getJar() {
        return jar;
    }

    public void setJar(String jar) {
        this.jar = jar;
    }

    public String getTargetServer() {
        return targetServer;
    }

    public void setTargetServer(String targetServer) {
        this.targetServer = targetServer;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public String getProjectToken() {
        return projectToken;
    }

    public void setProjectToken(String projectToken) {
        this.projectToken = projectToken;
    }
}

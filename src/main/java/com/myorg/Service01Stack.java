package com.myorg;

import software.amazon.awscdk.RemovalPolicy;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.ecs.AwsLogDriverProps;
import software.amazon.awscdk.services.ecs.Cluster;
import software.amazon.awscdk.services.ecs.ContainerImage;
import software.amazon.awscdk.services.ecs.LogDriver;
import software.amazon.awscdk.services.ecs.patterns.ApplicationLoadBalancedFargateService;
import software.amazon.awscdk.services.ecs.patterns.ApplicationLoadBalancedTaskImageOptions;
import software.amazon.awscdk.services.elasticloadbalancingv2.HealthCheck;
import software.amazon.awscdk.services.logs.LogGroup;
import software.constructs.Construct;

public class Service01Stack extends Stack {
    public Service01Stack(final Construct scope, final String id, Cluster cluster) {
        this(scope, id, null, cluster);
    }

    public Service01Stack(final Construct scope, final String id, final StackProps props, Cluster cluster) {
        super(scope, id, props);
        ApplicationLoadBalancedFargateService service01 = ApplicationLoadBalancedFargateService.Builder.create(this, "ALB01")
                .serviceName("service-01")
                .cluster(cluster)
                .cpu(512)
                .memoryLimitMiB(1024)
                .desiredCount(2)
                .listenerPort(8080)
                .assignPublicIp(true)
                .taskImageOptions(
                        ApplicationLoadBalancedTaskImageOptions.builder()
                                .containerName("aws_projet01")
                                .image(ContainerImage.fromRegistry("soza77/aws_project01:1.0.0"))
                                .containerPort(8080)
                                .logDriver(LogDriver.awsLogs(AwsLogDriverProps.builder()
                                        .logGroup(LogGroup.Builder.create(this, "Service01LogGroup")
                                                .logGroupName("service01")
                                                .removalPolicy(RemovalPolicy.DESTROY)
                                                .build())
                                        .build()))
                                .build())
                .publicLoadBalancer(true)
                .build();

        service01.getTargetGroup().configureHealthCheck(new HealthCheck.Builder()
                .path("/actuator/health")
                .port("8080")
                .healthyGrpcCodes("200")
                .build());
    }
}

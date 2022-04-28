package com.chinamobile.zj;

import com.chinamobile.zj.flowProcess.service.definition.FlowDefinitionResourceService;
import com.chinamobile.zj.flowProcess.service.resource.BaseExclusiveGatewayService;
import com.chinamobile.zj.flowProcess.service.resource.BaseSequenceFlowService;
import com.chinamobile.zj.flowProcess.service.resource.BaseStartNoneEventService;
import com.chinamobile.zj.flowProcess.service.resource.BaseUserTaskService;
import org.apache.commons.io.FileUtils;
import org.mybatis.spring.annotation.MapperScan;
import org.reflections.Reflections;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.reflections.scanners.Scanners.SubTypes;

@SpringBootApplication
@MapperScan("com.chinamobile.**.mapper*")
@Configuration
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public BaseSequenceFlowService getBaseSequenceFlowService() {
        return new BaseSequenceFlowService();
    }

    @Bean
    public BaseExclusiveGatewayService getBaseExclusiveGatewayService() {
        return new BaseExclusiveGatewayService();
    }

    /**
     *  todo zj 每次用的时候都是同一个... 这样不太好吧。。。
     * @return
     */
    @Bean
    public BaseStartNoneEventService getBaseStartNoneEventService() {
        return new BaseStartNoneEventService();
    }

    @Bean("flowDefinitionServiceMap")
    public Map<String, FlowDefinitionResourceService> getFlowDefinitionServiceMapBean() {
        Map<String, FlowDefinitionResourceService> definitionResourceId2DefinitionService = new HashMap<String, FlowDefinitionResourceService>() {
            {// todo zj 修改。
                String jsonXml;
//                File jsonFileOfProcess = new File("C:\\work\\zj\\process-china-moblie\\src\\main\\resources\\static\\precheck_install_process.json");
//                File jsonFileOfProcess = new File("C:\\projects\\china_mobile\\java\\process-china-moblie\\src\\main\\resources\\static\\precheck_install_process.json");
                File jsonFileOfProcess = new File("C:\\code\\java\\process-china-moblie\\src\\main\\resources\\static\\precheck_install_process.json");
                try {
                    jsonXml = FileUtils.readFileToString(jsonFileOfProcess,
                            Charset.defaultCharset());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                FlowDefinitionResourceService service1 = new FlowDefinitionResourceService(jsonXml);
                put(service1.getFlowProcessBo().getProperties().getProcessId(), service1);
            }
        };
        return definitionResourceId2DefinitionService;
    }

    @Bean("userTaskServiceClassMap")
    public Map<String, Class<? extends BaseUserTaskService>> getUserTaskServiceClassMapBean() throws InstantiationException, IllegalAccessException {
        Reflections reflections = new Reflections("com.chinamobile.zj.flowProcess.service.resource.userTask");
        Set<Class<?>> subTypes = reflections.get(SubTypes.of(BaseUserTaskService.class).asClass());
//        Set<Class<?>> annotated =
//                reflections.get(SubTypes.of(TypesAnnotated.with(SomeAnnotation.class)).asClass());

        Map<String, Class<? extends BaseUserTaskService>> definitionKey2BaseUserTaskServiceClass = new HashMap<>();
//        Map<String, Class<?>> definitionKey2BaseUserTaskServiceClass = new HashMap<>();
        for (Class<?> subType : subTypes) {
            if (Modifier.isAbstract(subType.getModifiers())) {
                // 忽略抽象类
                continue;
            }
            String instanceDefinitionKey = ((BaseUserTaskService) subType.newInstance()).getDefinitionKey();
            definitionKey2BaseUserTaskServiceClass.put(instanceDefinitionKey, (Class<BaseUserTaskService>) subType);

        }
//        definitionKey2BaseUserTaskServiceClass.put(StencilEnum.START_NONE_EVENT.getId(), BaseStartNoneEventService.class);
        return definitionKey2BaseUserTaskServiceClass;
    }

//    @Bean("userTaskServiceMap")
//    public Map<String, BaseUserTaskService> getUserTaskServiceClassMapBean() throws InstantiationException, IllegalAccessException {
//        Reflections reflections = new Reflections("com.chinamobile.zj.flowProcess.service.resource.userTask");
//        Set<Class<?>> subTypes = reflections.get(SubTypes.of(BaseUserTaskService.class).asClass());
////        Set<Class<?>> annotated =
////                reflections.get(SubTypes.of(TypesAnnotated.with(SomeAnnotation.class)).asClass());
//
//        Map<String, BaseUserTaskService> definitionKey2BaseUserTaskService = new HashMap<>();
//        for (Class<?> subType : subTypes) {
//            BaseUserTaskService instance = ((BaseUserTaskService) subType.newInstance());
//            String instanceDefinitionKey = instance.getDefinitionKey();
//            definitionKey2BaseUserTaskService.put(instanceDefinitionKey, instance);
//
//        }
//        definitionKey2BaseUserTaskService.put(StencilEnum.START_NONE_EVENT.getId(), new BaseStartNoneEventService());
//        return definitionKey2BaseUserTaskService;
//    }

//    @Bean("userTaskServiceMap")
//    public Map<String, BaseUserTaskService> getUserTaskServiceMapBean() {
//
//        Map<String, BaseUserTaskService> definitionKey2BaseUserTaskService = new HashMap<String, BaseUserTaskService>() {
//            {
//                BaseStartNoneEventService startNoneEvent = getBaseStartNoneEventService();
//                put(StencilEnum.START_NONE_EVENT.getId(), startNoneEvent);
//
//                BaseUserTaskService service1 = new SubmitPreCheckApplicationUserTaskService();
//                put(service1.getDefinitionKey(), service1);
//
//                BaseUserTaskService service3 = new ReviewApplicationAndAssignByCountyTieTongHdictUserTaskService();
//                put(service3.getDefinitionKey(), service3);
//
//                BaseUserTaskService service4 = new ReviewOrAssignApplicationByCountyHdictUserTaskService();
//                put(service4.getDefinitionKey(), service4);
//
//                BaseUserTaskService service2 = new ReviewApplicationByCountyJiaKeManagerUserTaskService();
//                put(service2.getDefinitionKey(), service2);
//            }
//        };
//        return definitionKey2BaseUserTaskService;
//    }
}

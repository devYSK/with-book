package core.di.beans.factory.support;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import core.di.context.annotation.ClasspathBeanDefinitionScanner;
import di.examples.MyQnaService;
import di.examples.MyUserController;
import di.examples.MyUserService;
import di.examples.QnaController;

public class BeanFactoryTest {
    private core.di.beans.factory.support.DefaultBeanFactory beanFactory;

    @BeforeEach
    public void setup() {
        beanFactory = new core.di.beans.factory.support.DefaultBeanFactory();
        ClasspathBeanDefinitionScanner scanner = new ClasspathBeanDefinitionScanner(beanFactory);
        scanner.doScan("di.examples");
        beanFactory.preInstantiateSinglonetons();
    }

    @Test
    public void constructorDI() throws Exception {
        QnaController qnaController = beanFactory.getBean(QnaController.class);
        assertNotNull(qnaController);
        assertNotNull(qnaController.getQnaService());

        MyQnaService qnaService = qnaController.getQnaService();
        assertNotNull(qnaService.getUserRepository());
        assertNotNull(qnaService.getQuestionRepository());
    }

    @Test
    public void fieldDI() throws Exception {
        MyUserService userService = beanFactory.getBean(MyUserService.class);
        assertNotNull(userService);
        assertNotNull(userService.getUserRepository());
    }

    @Test
    public void setterDI() throws Exception {
        MyUserController userController = beanFactory.getBean(MyUserController.class);

        assertNotNull(userController);
        assertNotNull(userController.getUserService());
    }

    @AfterEach
    public void tearDown() {
        beanFactory.clear();
    }
}

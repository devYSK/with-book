# 실전 스프링 부트

* https://product.kyobobook.co.kr/detail/S000208713876

* https://livebook.manning.com/book/spring-boot-in-practice
* https://github.com/spring-boot-in-practice/repo

[toc]



# CHAPTER 1 스프링 부트 시작하기 



## SpringBoot 스타트업 Event

스프링 이벤트 관리체계는 이벤트 publisher와 subscriber 분리를 강조한다.

애플리케이션 시작 및 초기화 과정에서 사용할 수 있는 이벤트는 다음과 같다.

| 이벤트명                              | 설명                                                         |
| ------------------------------------- | ------------------------------------------------------------ |
| `ApplicationStartingEvent`            | 애플리케이션이 시작되고 Listener가 등록되면 발행된다. 스프링 부트의 Logging system은 이 이벤트를 사용해서 애플리케이션 초기화 단계에 들어가기 전에 필요한 작업을 수행한다. |
| `ApplicationEnvironmentPreparedEvent` | 애플리케이션이 시작되고 Environment가 준비되면 발행된다. 스프링 부트는 이 이벤트를 사용해 MessageConverter, ConversionService, Jackson 초기화 등의 사전 초기화 작업을 수행한다. |
| `ApplicationContextInitializedEvent`  | ApplicationContext가 준비되고 ApplicationContextInitializers가 실행되면 발행된다. Bean이 컨테이너에 로딩되기 전에 수행해야 할 작업이 있을 때 이 이벤트를 사용한다. |
| `ApplicationPreparedEvent`            | ApplicationContext가 준비되고 빈이 로딩되었지만 아직 ApplicationContext가 리프레시되지 않은 시점에 발행된다. 이 이벤트 발행 후에는 Environment를 사용할 수 있다. |
| `ContextRefreshedEvent`               | ApplicationContext가 리프레시된 후에 발행된다. 이 이벤트는 스프링 부트가 아니라 스프링이 발행하는 이벤트이며, `SpringApplicationEvent`를 상속하지 않는다. ConditionEvaluationReportLoggingListener는 이 이벤트 발행 시 자동 구성 보고서를 출력한다. |
| `WebServerInitializedEvent`           | 웹 서버가 준비되면 발행된다. Servlet 기반 웹 애플리케이션에서는 `ServletWebServerInitializedEvent`, 리액티브 기반 웹 애플리케이션에서는 `ReactiveWebServerInitializedEvent`를 사용할 수 있다.  `SpringApplicationEvent`를 상속하지 않는다. |
| `ApplicationStartedEvent`             | ApplicationContext가 리프레시되고 나서 ApplicationRunner와 CommandLineRunner가 호출되기 전에 발행된다. |
| `ApplicationReadyEvent`               | 애플리케이션이 요청을 처리할 준비가 되면 발행된다. 이 이벤트 발행 후에는 모든 애플리케이션 초기화가 완료되었으므로, 이 시점 이후 애플리케이션 내부 상태 변경은 권장하지 않는다. |
| `ApplicationFailedEvent`              | 애플리케이션 시작 과정에서 예외가 발생하면 발행된다. 예외 발생 시 스크립트를 실행하거나 스타트업 실패를 알릴 때 사용된다. |

```java
@Slf4j
@Component
public class EventHandler{

    @EventListener(ContextRefreshedEvent.class)
    public void onContextRefreshedEvent(ContextRefreshedEvent event) {
        log.info("@@@ onContextRefreshedEvent !!!");        
    }
    
    @EventListener(ContextStartedEvent.class)
    public void onContextStartedEvent(ContextStartedEvent event) {
        log.info("@@@ onContextStartedEvent !!!");        
    }
    
    @EventListener(ContextStoppedEvent.class)
    public void onContextStoppedEvent(ContextStoppedEvent event) {
        log.info("@@@ onContextStoppedEvent !!!");        
    }

    @EventListener(ContextClosedEvent.class)
    public void onContextClosedEvent(ContextClosedEvent event) {
        log.info("@@@ onContextClosedEvent !!!");        
    }

    @EventListener(ApplicationContextInitializedEvent.class)
    public void onApplicationContextInitializedEvent(ApplicationContextInitializedEvent event) {
        log.info("@@@ onApplicationContextInitializedEvent !!!");        
    }
    
    @EventListener(ApplicationEnvironmentPreparedEvent.class)
    public void onApplicationEnvironmentPreparedEvent(ApplicationEnvironmentPreparedEvent event) {
        log.info("@@@ onApplicationEnvironmentPreparedEvent !!!");        
    }
    
    @EventListener(ApplicationPreparedEvent.class)
    public void onApplicationPreparedEvent(ApplicationPreparedEvent event) {
        log.info("@@@ onApplicationPreparedEvent !!!");        
    }
    
    @EventListener(ApplicationStartedEvent.class)
    public void onApplicationStartedEvent(ApplicationStartedEvent event) {
        log.info("@@@ onApplicationStartedEvent !!!");        
    }
    
    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReadyEvent(ApplicationReadyEvent event) {
        log.info("@@@ onApplicationReadyEvent !!!");        
    }

    @EventListener(ApplicationFailedEvent.class)
    public void onApplicationFailedEvent(ApplicationFailedEvent event) {
        log.info("@@@ onApplicationFailedEvent !!!");        
    }
    
    @EventListener(ApplicationStartingEvent.class)
    public void onApplicationStartingEvent(ApplicationStartingEvent event) {
        log.info("@@@ onApplicationStartingEvent !!!");        
    }

}
```

개별로 처리할수도 있다.

```java
@Slf4j
@Component
public class ApplicationReadyEvent implements ApplicationListener<ApplicationReadyEvent>{

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        log.info("@@@ ApplicationReadyEvent !!!");        
    }

}
```

### 이벤트 활용 예

1. Warm Up 코드 추가 

ApplicationReadyEvent 발생 시 서비스 관련된 부분(DB, Cache, Elastic Search, MQ 등)의 상태를 확인합니다. 

등록된 서비스를 테스트하고 L4 에서 확인하는 Health check 를 활성화 시켜 줍니다. 

 

2. 서비스 중지 이벤트 처리 

서비스를 중지 시키면 ContextClosedEvent 발생합니다. 이때 필요한 코드를 추가하면 됩니다. 

MQ를 사용하는 실 업무에서 이용가능. Warm up 시점에 서비스를 등록하고 중지 이벤트에서 해제해 줄 수 있다.

# CHAPTER 2 스프링 부트 공통 작업 

* 스프링 부트 애플리케이션 설정 관리

* @ConfigurationProperties를 사용한 커스텀 설정 정보 생성

* CommandLineRunner 인터페이스를 사용한 초기화 코드 실행

* 스프링 부트 기본 로깅과 Log4j2 로깅 설정

* 빈 밸리데이션을 사용한 사용자 입력 데이터 검증

## 로깅 패턴 및 경로 지정

```yml
// application.yml
logging:
  pattern:
    console: '%clr(%d{dd-MM-yyyy HH:mm:ss.SSS}){yellow} %clr(${PID:- }){green} %magenta([%thread]) %highlight([%-5level]) %clr(%-40.40logger{39}){cyan} %msg%n'
  file:
    path: 'C:\\sbip\\logs'

```

## Log4J2 사용

빌드 설정 파일에서 spring-boot-start-logging을 제거하고 Log4J2 스타터를 추가하면 된다.

```groovy
dependencies {
    implementation('org.springframework.boot:spring-boot-starter-web') {
        exclude group: 'org.springframework.boot', module: 'spring-boot-starter-logging'
    }
    implementation 'org.springframework.boot:spring-boot-starter-log4j2'
}
```

src/main/resources 디렉토리 아래에 log4j2.xml 또는 log4j2-spring.xml 파일을 만들면 된다

* https://github.com/spring-projects/spring-boot/issues/15649

```xml
<?xml version="1.0" encoding="UTF-8"?>
<Configuration status="WARN">
    <Properties>
        <Property name="LOG_PATTERN">
            %d{yyyy-MM-dd HH:mm:ss.SSS} [%5p] [%15.15t] %-40.40c{1.} : %m%n%ex
        </Property>
    </Properties>
    <Appenders>

        <Console name="ConsoleAppender" target="SYSTEM_OUT">
            <PatternLayout pattern="${LOG_PATTERN}"/>
        </Console>
        <RollingFile name="FileAppender"
                     fileName="logs/application-log4j2.log"
                     filePattern="logs/application-log4j2-%d{yyyy-MM-dd}-%i.log">
            <PatternLayout>
                <Pattern>${LOG_PATTERN}</Pattern>
            </PatternLayout>
            <Policies>
                <SizeBasedTriggeringPolicy size="10MB"/>
                <TimeBasedTriggeringPolicy interval="7"/>
            </Policies>
            <DefaultRolloverStrategy max="10"/>
        </RollingFile>
    </Appenders>
    <Loggers>
        <Logger name="com.manning.sbip" level="debug" additivity="false">
            <AppenderRef ref="FileAppender"/>
        </Logger>
        <Logger name="org.springframework.boot" level="info" additivity="false">
            <AppenderRef ref="ConsoleAppender"/>
        </Logger>
        <Root level="info">
            <AppenderRef ref="FileAppender"/>
            <AppenderRef ref="ConsoleAppender"/>
        </Root>
    </Loggers>
</Configuration>

```

* https://logging.apache.org/log4j/2.x/ 참고

## 발리데이션 커스텀 어노테이션 적용

### Passay 비밀번호 검증 라이브러리

* https://www.passay.org/

```groovy
dependencies {
    implementation 'org.passay:passay:1.6.2'
    implementation 'org.springframework.boot:spring-boot-starter-validation'
    implementation 'org.hibernate.validator:hibernate-validator'
}
```



코드

```java
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordRuleValidator.class)
public @interface Password {
    String message() default "Password do not adhere to the specified rule";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

// 
public class PasswordRuleValidator implements ConstraintValidator<Password, String> {

	private static final int MIN_COMPLEX_RULES = 2;
	private static final int MAX_REPETITIVE_CHARS = 3;
	private static final int MIN_SPECIAL_CASE_CHARS = 1;
	private static final int MIN_UPPER_CASE_CHARS = 1;
	private static final int MIN_LOWER_CASE_CHARS = 1;
	private static final int MIN_DIGIT_CASE_CHARS = 1;

	@Override
	public boolean isValid(String password, ConstraintValidatorContext context) {
		List<Rule> passwordRules = new ArrayList<>();
		passwordRules.add(new LengthRule(8, 30));
		CharacterCharacteristicsRule characterCharacteristicsRule =
			new CharacterCharacteristicsRule(MIN_COMPLEX_RULES,
				new CharacterRule(EnglishCharacterData.Special, MIN_SPECIAL_CASE_CHARS),
				new CharacterRule(EnglishCharacterData.UpperCase, MIN_UPPER_CASE_CHARS),
				new CharacterRule(EnglishCharacterData.LowerCase, MIN_LOWER_CASE_CHARS),
				new CharacterRule(EnglishCharacterData.Digit, MIN_DIGIT_CASE_CHARS));

		passwordRules.add(characterCharacteristicsRule);
		passwordRules.add(new RepeatCharacterRegexRule(MAX_REPETITIVE_CHARS));
		PasswordValidator passwordValidator = new PasswordValidator(passwordRules);
		PasswordData passwordData = new PasswordData(password);
		RuleResult ruleResult = passwordValidator.validate(passwordData);
		return ruleResult.isValid();

	}
}

```



사용

```java
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

public void run(String... args) throws Exception {

	final var user = new User("test", "test");

	Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

	Set<ConstraintViolation<User>> violations = validator.validate(user);
	log.error("user1의 비밀번호가 비밀번호 정책을 준수하지 않습니다.");
	violations.forEach(constraintViolation -> log.error("Violation details: [{}].", constraintViolation.getMessage()));

}
```



# CHAPTER 3 스프링 데이터를 사용한 데이터베이스 접근

스프링 데이터의 목표 : 여러 데이터 소스(몽고,  mybatis, jpa)를 다룰 때 일관성있는 모델을 제공하기 위함.

**스프링 데이터 모듈들**

* https://spring.io/projects/spring-data

| 모듈 이름                                     | 목적                                                         |
| --------------------------------------------- | ------------------------------------------------------------ |
| 스프링 데이터 커먼즈commons                   | 모든 스프링 데이터 프로젝트에서 사용하는 기초 컴포넌트       |
| 스프링 데이터 JDBC                            | JDBC에 사용할 수 있는 리포지터리 지원                        |
| 스프링 데이터 JPA                             | JPA에 사용할 수 있는 리포지터리 지원                         |
| 스프링 데이터 몽고DB                          | 도큐먼트 기반 몽고DB 데이터베이스 지원                       |
| 스프링 데이터 레디스                          | 레디스Redis 데이터 스토어 지원                               |
| 스프링 데이터 REST                            | 스프링 데이터 리포지터리를 REST 리소스로 사용할 수 있도록 지원 |
| 스프링 데이터 아파치 카산드라Apache Cassandra | 아차피 카산드라 데이터 스토어 지원                           |

![image-20230916215724625](./images//image-20230916215724625.png)

## 몽고 디비 설정 방법 - spring-boot-starter-data-mongo

```groovy
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-data-mongodb'
    implementation 'de.flapdoodle.embed:de.flapdoodle.embed.mongo'
}
```

* 내장형 Flapdoodle 몽고DB
  * 간편한 사용을 위해 내장형 몽고DB인 Flapdoodle 를 사용
  * https://github.com/flapdoodle-oss/de.flapdoodle.embed.mongo
  * Flap-doodle 몽고DB는 몇 가지 한계가 있어서 실제 운영 환경이나 복잡한 애플리케이션에서는 사용하지 않는 것이 좋다.
  *  자세한 내용은 https://mng.b2Yg5A를 참고한다. 
  * 실제 운영 환경에서는 몽고DB를 사용하거나 테스트 목적 이라면 테스트컨테이너(https://www.testcontainers.org/)를 사용하는 것이 좋다.

```java
@DataMongoTest
@ExtendWith(SpringExtension.class)
public class MongoTest {

	@Autowired
	private MongoTemplate mongoTemplate;

	@Test
	void 몽고_컬렉션_테스트() {
		// given
		DBObject object = BasicDBObjectBuilder.start().add("Manning", "Spring Boot In Practice").get();
		// when

		mongoTemplate.save(object, "collection");
		// then
		Assertions.assertThat(mongoTemplate.findAll(DBObject.class, "collection"))
			.extracting("Manning")
			.containsOnly("Spring Boot In Practice");

	}
}
```

* https://github.com/spring-boot-in-practice/repo/wiki/Beginners-Guide-to-MongoDB

## 커스텀 스프링 데이터 레포지토리

필요한 CRUD 메서드만 노출하는 Repository

```java
@NoRepositoryBean
public interface BaseRepository<T, ID>  extends Repository<T, ID> {

    <S extends T> S save(S entity);

    Iterable<T> findAll();

}
//

@Repository
public interface CustomizedCourseRepository extends BaseRepository<Course, Long> {
}

```

* 스프링 데이터가 자동으로 구현체를 만들지 않도록  @NoRepositoryBean 사용

# CHAPTER 4 스프링 자동 구성과 액추에이터

## 커스텀 스프링 부트 실패 분석기

요구사항 : 외부 API 서비스를 사용 불가하면 애플리케이션이 시작되면 안된다.

- ﻿﻿스프링 부트의 ContextRefreshedEvent를 사용해서 검증 프로세스를 구동한다. 
  - ApplicationContext가 갱신되면 ContextRefreshedEvent를 발행한다.
- ﻿﻿API가 사용할 수 없는 상태라면 개발자가 작성한 런타임 예외인 UrlNotAccessibleException예외를 던진다.
- ﻿﻿UrlNotAccessibleException 예외가 던져지면 호출되는 UrINotAccessibleFailureAnalyzer를 작성한다.
- ﻿﻿마지막으로 spring.factories 파일에 UrINotAccessibleFailureAnalyzer를 한다. 
- src/main/ java/META-INE 디렉터리에 있는 spring factories 파일은 애플리케이션 시작 시점에 스프링으 로 로딩하는 특수 파일로서 여러 가지 설정 클래스에 대한 참조가 포함돼 있다.

```java
@Getter
public class UrlNotAccessibleException extends RuntimeException {

	private String url;
	
	public UrlNotAccessibleException(String url) {
		this(url, null);
	}

	public UrlNotAccessibleException(String url, Throwable cause) {
		super("URL " + url + " is not accessible", cause);
		this.url = url;
	}
}
//

@Component
public class UrlAccessibilityHandler {

	@Value("${api.url:https://dog.ceo/}")
	private String url;

	@EventListener(classes = ContextRefreshedEvent.class)
	public void onListen() {
		// 데모 목적으로 일부러 예외 발생
		throw new UrlNotAccessibleException(url);
	}
}

// 
public class UrlNotAccessibleFailureAnalyzer extends AbstractFailureAnalyzer<UrlNotAccessibleException> {

	@Override
	protected FailureAnalysis analyze(Throwable rootFailure, UrlNotAccessibleException cause) {
		return new FailureAnalysis("액세스 할 수 없습니다. URL : " + cause.getUrl(),
			"URL의 유효성을 검사하고 액세스 가능한지 확인하세요", cause);
	}

}

```

* 예외가 발생하면 스프링 부트는 FailureAnalyzer를 호출한다.
* 이를 위해 예외를 처리할 수 있는 FailureAnalyzer를 src/main/java/META-INF/spring.factories 파일에 작성한다

```properties
org.springframework.boot.diagnostics.FailureAnalyzer=\
com.ys.practice.fail.UrlNotAccessibleFailureAnalyzer
```

* 복수개로 추가도 가능하다

```properties
org.springframework.boot.diagnostics.FailureAnalyzer=\
com.ys.practice.fail.UrlNotAccessibleFailureAnalyzer,
~~~,
~~~
```



## 액추에이터 엔드포인트 변경

```properties
management:
  endpoints:
    web:
      base-path: /ys
      path-mapping:
        health: apphealth
  server:
    port: 8081

```



## 커스텀 스프링 부트 HealthIndicator 작성

요구사항 : 외부  Rest API 시스템 상태 모니터링

```java

@Component
public class DogsApiHealthIndicator implements HealthIndicator {

	public Health health() {
		try {
			ParameterizedTypeReference<Map<String, String>> reference = new ParameterizedTypeReference<>() {
			};
			
			
			ResponseEntity<Map<String, String>> result = new RestTemplate().exchange(
				"https://dog.ceo/api/breeds/image/random", HttpMethod.GET, null, reference);

			if (result.getStatusCode()
					  .is2xxSuccessful() && result.getBody() != null) {
				return Health.up()
							 .withDetails(result.getBody())
							 .build();
			} else {
				return Health.down()
							 .withDetail("status", result.getStatusCode())
							 .build();
			}
		} catch (RestClientException ex) {
			return Health.down()
						 .withException(ex)
						 .build();
		}
	}
}

```

yml 설정

```yaml
management:
  endpoints:
    web:
      base-path: /ys
  server:
    port: 8081
  endpoint:
    health:
      show-details: always

```

endpoint 호출

```http
http://localhost:8081/ys/health
```

## 액추에이터 info endpoint



`info` 엔드포인트는 애플리케이션에 관한 임의의 애플리케이션 정보를 제공합니다. 

일반적으로 애플리케이션의 메타데이터, 버전, 설명, 사용자 지정 정보 등과 같은 비즈니스적 관점에서 중요한 정보를 제공하는 데 사용

application.yml에 

 info: build.* 프로퍼티를 추가하면 pom.xml, gradle 파일에 명시된 artifactId, groupId, version 정보도 info 엔드포인트에서 표시할 수 있다.

* maven의  properties를 참조하는법

```yaml
info:
  app:
    name: ys Spring Boot Actuator Info Application
    description: Spring Boot application that explores the /info endpoint
    version: 1.0.0
  build:
    artifact: "@project.artifactId@"
    name: "@project.name@"
    description: "@project.description@"
    version: "@project.version@"
    properties:
      java:
        version: "@java.version@"
```

* gradle의  properties를 참조하는법

```yaml
info:
  app:
    name: ys Spring Boot Actuator Info Application
    description: Spring Boot application that explores the /info endpoint
    version: 1.0.0
  build:
    artifact: ${project.artifactId}
    name: ${project.name}
    description: ${project.description}
    version: ${project.version}
    properties:
      java:
        version: ${java.version}
```

build.gradle에서 다음도 해야한다.

```groovy
springBoot {
    buildInfo()
}
```

## info엔드포인트에서 git 정보 보기

git.properties 파일과 build-info.properties 파일은 각각

GitInfoContributor 클래스와 Build-InfoContributor 클래스를 통해 자동으로 인식된다.

Gradle 에선 이미 `build-info` task를 제공하므로 추가 설정 없이 빌드 정보를 생성할 수 있다.

```yaml
springBoot {
    buildInfo()
}
```

plugin 추가

```yaml
plugins {
    id 'com.gorylenko.gradle-git-properties' version '2.3.1'
}
```

info endpoint 접속시 git 정보가 보인다

git 정보는 application.properties 파일의 management. info.git.mode 프로퍼티값을 기준으로 표시된다. 

기본값은 simple이며 commit과 branch 정보만 표시된다. 

full로 지정하면 git.properties에 있는 모든 정보가 표시된다

```yaml
management:
  info:
    git:
      mode: full
```

## info 엔드포인트에서 애플리케이션 정보 커스텀하기

애플리케이션 상세 정보를 스프링 부트 액추에이터 엔드포인트를 통해 표시해야 한다.

스프링 부트의 InfoContributor 인터페이스 구현체를 만들면 스프링 부트 액추에이터의 info 엔드포인트에 원하는 정보를 표시할 수 있다.

contribute() 메서드를 재정의하면 된다.

```java
@Component
public class CourseInfoContributor implements InfoContributor {

    private CourseService courseService;

    @Autowired
    public CourseInfoContributor(CourseService courseService) {
        this.courseService = courseService;
    }

    @Override
    public void contribute(Info.Builder builder) {
        Map<String, Integer> courseNameRatingMap = new HashMap<>();
        List<CourseNameRating> courseNameRatingList = new ArrayList<>();
        for(Course course : courseService.getAvailableCourses()) {
            courseNameRatingList.add(CourseNameRating.builder().name(course.getName()).rating(course.getRating()).build());
        }
        builder.withDetail("courses", courseNameRatingList);
    }

    @Builder
    @Data
    private static class CourseNameRating {
        String name;
        int rating;

    }
}

```

## 애플리케이션 상세 정보 커스텀 엔드포인트

애플리케이션에 특화된 비즈니스 상세 정보를 제공할 수 있는 스프링 부트 액추에이터 엔드포인트를 작성해야 한다.

스프링 부트에서 제공하는 @EndPoint 애너테이션을 붙여서 커스텀 엔드포인트를 추가할 수 있다.

```java
@Component
@Endpoint(id = "releaseNotes")
//@JmxEndpoint(id = "releaseNotes")
public class ReleaseNotesEndpoint {

	private final Collection<ReleaseNote> releaseNotes;

	@Autowired
	public ReleaseNotesEndpoint(Collection<ReleaseNote> releaseNotes) {
		this.releaseNotes = releaseNotes;
	}

	@ReadOperation
	public Iterable<ReleaseNote> releaseNotes() {
		return releaseNotes;
	}

	@ReadOperation
	public Object selectCourse(@Selector String version) {
		Optional<ReleaseNote> releaseNoteOptional = releaseNotes
			.stream()
			.filter(releaseNote -> version.equals(releaseNote.getVersion()))
			.findFirst();
		if(releaseNoteOptional.isPresent()) {
			return releaseNoteOptional.get();
		}
		return String.format("No such release version exists : %s", version);
	}

	@DeleteOperation
	public void removeReleaseVersion(@Selector String version) {
		Optional<ReleaseNote> releaseNoteOptional = releaseNotes
			.stream()
			.filter(releaseNote -> version.equals(releaseNote.getVersion()))
			.findFirst();
		if(releaseNoteOptional.isPresent()) {
			releaseNotes.remove(releaseNoteOptional.get());
		}
	}
}
```

이후 지정한 id를 노출시켜야하므로 application.yml에 추가한다

```yaml
management:
	endpoints:
		web:
			exposure:
				include: releaseNotes
```

http://localhost:8081/ys/releaseNotes로 접속

## 스프링부트 액츄에이터 metric

micrometer 프레임워크를 사용하여 측정지표를 사용한다

프로메테우스 말고 다른 모니터링 시스템 을 사용하려면 

`micrometer-registry-{monitoring_system}` 의존 관계를 추가하면 스프링 부트 가 자동 구성으로 해당 모니터링 시스템을 사용할 수 있게 해준다.

### 측정지표 노출 

```yaml
management:
  metrics:
    export:
      defaults:
        enabled: true
```



### 커스텀 메트릭

NeterRegistry를 사용해서 자동 구성으로 여러 개의 레지스트리 구현체를 추가 할 수 있다.

 그래서 한 개 이상의 모니터링 시스템에 측정지표를 내보내서 사용할 수 있다. 

또한 MeterRegistryCustonizer를 사용해서 레지스트리 커스터마이징도 가능하다

<img src="./images//image-20230916234645109.png">

모든 측정지표는 스프링 부트의 `JvmMetricsAutoConfiguration` 클래스를 통해 자동 구성 된다.

마이크로미터 프레임워크는 커스텀 측정지표를 생성할 때 사용할 수 있는 

카운터(counter), 

게이지 (Gauge), 

타이머(Timer), 

분포 요약(Distributionsummary)과 같은 다양한 측정 단위를 제공한다

### Counter

Counter는 증가할 수 있는 갯수나 횟수를 의미한다 

```java
@Configuration
public class CourseTrackerMetricsConfiguration {

    @Bean
    public Counter createCourseCounter(MeterRegistry meterRegistry) {
        return Counter.builder("api.courses.created.count")
                .description("Total number of courses created")
                .register(meterRegistry);
    }
}

```

### Gauge

Counter의 단점은 애플리케이션 종료 후에 카운터값이 유지되지 않고 애플리케이션이 재시작되면 0으로 초기화된다는 점이다.

 따라서 생성된 과정의 개수를 애플리케이션 종료 후에도 추적하려면 Counter를 사용할 수 없다.

게이지Gauge를 사용하면 된다. 

게이지는 카운터와 마찬가지로 개수나 횟수를 셀 수 있지만, 애플리케이션이 종료되면 값이 초기화되는 카운터와 달리,

 데이터베이스를 이용해서 값을 저장하고  Gauge 측정지표를 통해서 값을 확인할 수 있다.

 ```java
 @Configuration
 public class CourseTrackerMetricsConfiguration {
 
     @Bean
     public Gauge createCoursesGauge(MeterRegistry meterRegistry, CourseService courseService) {
         return Gauge.builder("api.courses.created.gauge", courseService::count)
                 .description("Total courses created")
                 .register(meterRegistry);
     }
 
 }
 ```

### Timer

때때로 어떤 연산을 수행할 때 소 요되는 시간을 측정해야 할 때도 있다.

```java
@Configuration
public class CourseTrackerMetricsConfiguration {

    @Bean
    public Timer createCoursesTimer(MeterRegistry meterRegistry) {
        return Timer.builder("api.courses.creation.time")
                .description("Course creation time")
                .register(meterRegistry);
    }
}

public class Service {
  private final Timer createCoursesTimer;
  
  public Course createCourse(Course course) {
    return createCoursesTimer.recordCallable(() -> courseRepository.save(course));
  }
}
```

타이머는 내부적으로 Callable 객체 안에서 과정 생성 시 소요되는 시간을 측정한다.

### Distrubution Summary

분포 요약distribution summary은 이벤트의 분포를 측정한다.

Timer와 구조적으로는 비슷하지만 측정 단위가 시간이 아니라는 점에서 차이가 있다

```java
@Configuration
public class CourseTrackerMetricsConfiguration {
    @Bean
    public DistributionSummary createDistributionSummary(MeterRegistry meterRegistry) {
        return DistributionSummary.builder("api.courses.rating.distribution.summary")
               .description("Rating distribution summary")
                .register(meterRegistry);
    }
}

```

## 그라파나 프로메테우스 연동

스프링 부트는 클래스패스에 프로메테우스 라이브러리가 있으면 스 프링 부트에 내장된 측정지표 및 커스텀 측정지표 전부를 프로메테우스에게 전송한다.

프로메테우스의 측정지표 형식은 스프링 부트와 다른데 전체 목록은 https://mng.bz/aJMz를 참고. 

프로메테우스의 측정지표를 사용해서 그라파나로 시각화 설정까지 살펴보자.

* https://github.com/spring-boot-in-practice/repo/wiki/Sample-Prometheus-Metrics

```yaml
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-actuator'
    implementation 'io.micrometer:micrometer-registry-prometheus'
}
```

```yaml
management:
  endpoints:
    web:
      exposure:
        include: metrics,prometheus
```

프로메테우스 의존 관계를 추가하면 `PrometheusMetricsexportAutoConfiguration` 클래스가 활성화되고 `PrometheusMeterRegistry` 빈이 등록된다. `PrometheusMeterRegistry` 빈이 플러그인으로 추가되어 측정지표 파사드 역할을 담당한다.

# CHAPTER 5 스프링 부트 애플리케이션 보안

* 스프링 시큐리티 개요와 일반적인 보안 위협

* 스프링 시큐리티 적용과 스프링 시큐리티 자동 구성 이해

- ﻿﻿인메모리, JDBC, LDAP 환경에서 스프링 시큐리티 커스터마이징
- ﻿﻿스프링 부트 프로젝트에 HTTP 기본 인증 적용



# CHAPTER 6 스프링 시큐리티 응용



# CHAPTER 7 스프링 부트 RESTful 웹 서비스 개



# CHAPTER 8 리액티브 스프링 부트 애플리케이션 개발



# CHAPTER 9 스프링 부트 애플리케이션 배포



# CHAPTER 10 스프링 부트와 코틀린, 네이티브 이미지, GraphQ
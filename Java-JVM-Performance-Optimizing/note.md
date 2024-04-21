# JVM 퍼포먼스 옵티마이저 및 성능분석

[toc]



# JVM 메모리 구조

![image-20240130214354002](./images//image-20240130214354002.png)

* JavaCompiler: Source파일을 ByteCode로 변경
  * ByteCode : .class파일
* Class Loader: JVM내로 .class 파일들을 Load하고 Runtime Data Area에 배치
* Execution Engine : 로딩된 .class의 bytecode를 해석한다

JVM은 컴파일 과정을 통하여 생성된 Class 파일을 JVM으로 로딩하고, ByteCode를 해석(interpret)하는 과정을 거쳐 메모리 등의 리소스를 할당하고 관리하며 정보를 처리한다. 

## JVM 메모리 구조

![image-20240130214605977](./images//image-20240130214605977.png)

* Method Area : 클래스, 변수, Method, static변수, 상수 등이 저장되는 영역이며 `모든 쓰레드가 공유한다`.
* Heap Area: new 명령어로 생성된 인스턴스와 객체가 저장되는 구역. `모든 쓰레드가 공유한다.`
* Stack Area: 메소드 내에서 사용되는 지역변수, 매개변수, 리턴값 등이 저장되는 구역으로, 메소드가 호출될때 후입선출로 하나씩 생성되고, 메소드 실행이 완료되면 하나씩 지워진다.` 각 쓰레드별로 하나씩 생성된다.`
* PC Register: CPU의 Register와 비슷하다. 현재 수행중인 JVM 명령의 주소값이 저장된다. `각 쓰레드별로 하나씩 생성된다`
* Native Method Stack: 다른 언어의 네이티브 메소드를 호출하기 위해 할당되는 구역.

## Java Heap

Heap 영역은 Instance와 Array 객체 두가지만 저장되는 공간이다. 

객체가 공유되므로 모든 쓰레드들이 접근할 수 있어. 동기화 이슈가 발생할 수 있다.

## Hotspot JVM Heap 구조

가장 기본적인 JVM

![image-20240130215310520](./images//image-20240130215310520.png)

Young Generation은 Eden영역과 Suvivor 1,2영역으로 구성된다.

Eden영역은 객체가 힙에 최초로 할당되는 장소이며, Eden영역이 꽉 차게되면 객체의 참조 여부를 따져 만약 참조가 되고있는 객체라면 Survior영역으로 넘기고 참조가 끊어진 객체이면 그냥 남겨놓는다. 모든 참조되는 객체가 Survivor영역으로 넘어가면 Eden영역을 가비지 컬렉터한다.



Survivor영역은 Eden에서 살아남은 객체들이 잠시 머무른다. 2개의 Survivor 영역중 Eden에서 살아있는 객체를 대피시킬때는 하나의 영역만 사용한다.

이 과정들을 Minor GC라고 한다.



Young Generation에서 오래 살아남은 객체는 OldGeneration으로 이동한다. 

* 오래살아남은 객체는 특정 회수 이상 참조되어 기준 Age를 초과한 객체

OldGeneration 영역도 메모리가 충분하지 않으면 Full GC(Major GC)가 발생한다.



Perm 영역은 클래스, 메소드의 메타정보, static 변수와 상수 정보들이 저장되는 영역이다.

* 자바 8부터는 Static Object는 Heap 영역으로 옮겨져서 GC의 대상이 될수도 있다.

<img src="./images//image-20240130215736817.png" width = 800 height = 550>

| 구분        | 상세 구분                | 자바 7 (Perm)                           | 자바 8 (Metaspace)                                           |
| ----------- | ------------------------ | --------------------------------------- | ------------------------------------------------------------ |
| 저장 정보   | Class의 Meta 정보        | 저장                                    | 저장                                                         |
|             | Method의 Meta 정보       | 저장                                    | 저장                                                         |
|             | Static Object 변수, 상수 | 저장                                    | Heap 영역으로 이동                                           |
| 관리 포인트 | 메모리 관리 (튜닝)       | Heap 영역 튜닝 외에 Perm 영역 별도 튜닝 | Heap 영역 튜닝, Native 영역 동적 조정 (별도 옵션으로 조절 가능) |
| GC 측면     | GC 수행 대상             | Full GC 수행 대상                       | Full GC 수행 대상                                            |
| 메모리 측면 | 메모리 크기 (옵션)       | -XX:PermSize, -XX:MaxPermSize           | -XX:MetaspaceSize, -XX:MaxMetaspaceSize                      |

* 자바 7과 자바8의 Perm -> Metaspace 변경 사항

# GC 

가비지 컬렉터.

System.GC()를 명시적으로 사용하면, Full GC가 발생한다



## Root Set과 Garbage

객체의 사용여부는 Root Set과의 관계로 판단하게 된다.

어떤식으로든 Reference 관계가 있다면 Reachable Object라고 하며 이는 현재 사용하고 있는 객체로 볼 수 있따.

 ![image-20240130221016143](./images//image-20240130221016143.png)

아래 3가지 참조 형태를 통해 Reachable Object를 판별한다.

* Local variable Section, Operande stack에 객체의 참조정보가 있다면 Reachable
  * 이 둘은 Stack 메모리 구조에 해당된다. Local variable Section는 로컬 변수, Operand Stack은 연산을 수행하기 위한 스택. int a = b + c; 연산 수행시 b, c는 여기에 저장
* Method Area에 로딩된 클래스 중 constant pool에 있는 정보를 토대로 쓰레드에서 직접 참조하진 않지만 constant pool을 통해 간첩 link를 하고있다면 Reachable
* 아직 메모리에 있고, Native Method Area의 JNI 객체가 참조관계가 있는경우

이 3가지를 제외하면 모두 GC 대상이다.

메모리 릭은 언제 발생할까?

```java
class Leak {
  List list = new ArrayList();
  
  public void removeStr(int i) {
    Object obj = list.get(i);
    obj = null; // 이부분 주의 
  }
  
  public void add(int a) {
    list.add("가나다라" + a);
  }
}

main {
  Leak leak = new Leak();
  for (int i = 0; i < 9999999; i++) {
    leak.add(i);
    leak.removeStr(i);
  }
}
```

이 코드는 메모리 릭이다.

list.get(i)를 호출해서 obj로 받은것은 String이 아닌, String 객체로 접근하는 Reference 값이기 때문이다. 

add()메소드에서 list = null을 초기화해도 ArrayList에 들어가있는 객체가 아니기 때문에 GC에 수집되지 않는다.

이것이 바로 Reachable but not Live 객체이다. 

* null로 치환돼도 Array List에 들어간 String Obiect가 사라진 건 아니며 더불어 참조변수가

  없어서 이들은 앞으로 사용되지 않을 것이다.

## GC 목적

메모리가 필요하면 수행된다. 

힙메모리도 메모리기 때문에 단편화 문제가 발생한다. 각 GC는 이 문제를 해결하기 위해 Compaction(압축)과 같은 알고리즘을 사용한다. 



# Hotspot JVM의 GC

GC 메커니즘은 두가지 가설을 두고있다.

1. 대부분의 객체는 생성된 후 금방 가비지가 된다.
2. 오래된 객체가 새로 생긴 객체를 참조할 일은 드물다.

첫번째 가설을 보면 새로 할당되는 객체가 모이는 곳은 금방 수거되므로 단편화 발생 확률이 높다. 

메모리 할당은 기존 객체 바로 다음 주소에서 수행이 되기 때문이다.

이때 Sweep작업 (Mark되지 않은 객체 제거)를 수행하면 단편화가 발생하며 이후 Compaction같은 비싼 작업을 해야한다.

`단편화 때문에 핫스팟은 할당만을 위한 공간인 Eden 영역을 만들고, GC시 살아있는 객체들을 피신시키는 Survivor영역을 따로 구성한것이다. `* 

* 즉 가비지가 될 확률이 적은 객체는 따로 관리하겟다는 목적.

Root Set에서 참조관계를 추적해서 마크하는 작업은, 오래된 객체가 젊은 객체를 참조하는 상황이 발생하면 suspend time이 길어지므로 핫스팟은 Card Table(이벤트 프로그램)이란 장치를 마련했다.

**Card Table**

 CardTable이란 Old 영역의 Memory를 대표하는 별도의 영역이다. 만약 젊은 객체를 참조하는 오래된 객체가 있다면,

오래된 객체의 시작주소에 카드(Flag)를 표시해서 카드 테이블에 기록하고, 참조가 끊어지면 표시한 카드도 사라지게 해서 참조 관게를 쉭베 파악할 수 있다. 

* Card는 Old Ara의 512바이트당 1바이트를 차지한다.

## GC 대상 범위

Young, Old, Permanent 영역이 GC 대상이다.

<img src="./images//image-20240130222940824.png" width = 800>

핫스팟은 각 영역별로 GC를 수행한다.

Young은 `Minor GC`라고 부르며 빈번하게 수행된다.

Old 영역은 `Full GC(메이저)`라고 말한다.

Permanent 영역의 메모리가 부족해도 GC가 발생하는데, 너무 많은 클래스가 로딩되어 메모리가 부족하기 때문이다.

* Permanent의 GC는 Full GC이다. 

## Hotspot GC 관련 옵션

![image-20240130223017319](./images//image-20240130223017319.png)

| 옵션                               | 상세 설명                                                    |
| ---------------------------------- | ------------------------------------------------------------ |
| `-Client`                          | Client Hotspot VM으로 구동한다. (Client Class)               |
| `-Server`                          | Server Hotspot VM으로 구동한다. (Server Class)               |
| `-Xms<Size>`                       | Young Generation의 최소 크기를 설정한다.                     |
| `-Xmx<Size>`                       | Young Generation의 최대 크기를 설정한다.                     |
| `-XX:NewSize=<Size>`               | Intel CPU에서는 기본값 640kbytes, 그 외에는 2.124Mbytes.     |
| `-Xss<Size>`                       | 스택 사이즈를 설정한다.                                      |
| `-XX:MaxNewSize=<Size>`            | Young Generation의 최대 크기를 지정한다. 1.4 버전 이후 NewRatio에 따라 자동 계산된다. |
| `-XX:PermSize`                     | Permanent Area의 초기 크기를 설정한다.                       |
| `-XX:MaxPermSize`                  | Permanent Area의 최대 크기를 설정한다. (기본값=64Mbytes)     |
| `-XX:SurvivorRatio=<value>`        | 값이 n이면 n:1:1 (Eden:Survivor 1:Survivor 2) 비율이다. (기본값=8) |
| `-XX:NewRatio=<value>`             | 값이 n이면 Young:Old 비율은 1:n (Client Class 기본값=8, Server Class 기본값=2, Intel CPU 사용 시 기본값=12) |
| `-XX:TargetSurvivorRatio=<value>`  | Survivor Area가 Minor GC를 유발하는 비율. 기본값=50 (즉, Survivor Area가 50% 차면 Minor GC 발생) |
| `-XX:MinHeapFreeRatio=<percent>`   | 전체 Heap 대비 Free Space가 지정된 수치 이하면 Heap을 -Xmx로 지정된 수치까지 확장한다. (기본값=40) |
| `-XX:MaxHeapFreeRatio=<percent>`   | 전체 Heap 대비 Free Space가 지정된 수치 이상이면 -Xms까지 축소한다. (기본값=70) |
| `-XX:MaxTenuringThreshold=<value>` | Value 만큼 SS1, SS2를 이동하면 Old Generation으로 Promotion 한다. |
| `-XX:+DisableExplicitGC`           | `System.gc()` 함수를 통한 수동 GC를 방지한다.                |

```
최소 힙 크기 설정:
java -Xms512m MyApplication

명시적 가비지 컬렉션 호출 무시:
java -XX:+DisableExplicitGC MyApplication

최대 힙 크기 설정:
java -Xmx1024m MyApplication
```

## Garbage Collector 종류

### 가비지 컬렉터 옵션 표

| Garbage Collector             | Option                    | Young Generation Collection 알고리즘 | Old Generation Collection 알고리즘 |
| ----------------------------- | ------------------------- | ------------------------------------ | ---------------------------------- |
| Serial Collector              | `-XX:+UseSerialGC`        | Serial                               | Serial Mark-Sweep-Compact          |
| Parallel Collector            | `-XX:+UseParallelGC`      | Parallel Scavenge                    | Serial Mark-Sweep-Compact          |
| Parallel Compacting Collector | `-XX:+UseParallelOldGC`   | Parallel Scavenge                    | Parallel Mark-Sweep-Compact        |
| CMS Collector                 | `-XX:+UseConcMarkSweepGC` | Parallel                             | Concurrent Mark-Sweep              |
| G1 Collector                  | `-XX:+UseG1GC`            | Snapshot-At-The-Beginning (SATB)     | Snapshot-At-The-Beginning (SATB)   |

```
Serial Collector 사용:
java -XX:+UseSerialGC MyApplication

Parallel Collector 사용:
java -XX:+UseParallelGC MyApplication

Parallel Compacting Collector 사용:
java -XX:+UseParallelOldGC MyApplication

CMS Collector 사용:
java -XX:+UseConcMarkSweepGC MyApplication

G1 Collector 사용:
java -XX:+UseG1GC MyApplication
```



java 8 : Parallel GC

java 9 이후 G1GC



### Serial Collector

Single CPU 즉 1개 쓰레드를 가지고 GC를 수행한다. 

### Parallel Collector

Young 영역에서의 컬렉션을 병렬로 처리한다. 

* 목표 : 다른 CPU가 대기상태로 남아있는것을 최소화 하자.

멀티스레드가 동시에 GC를 수행하며 적용범위는 Young 영역이다.

Old 영역은 Mark-Sweep-Compact 알고리즘이 사용되며 싱글 스레드 방식이다. 

### CMS Collector

low-latency collector. 힙 영역의 크기가 클때 적합하다.

CMS는 suspend time을 분산하여 응답 시간을 개선한다.

목표 : 여유 메모리 있는 상태에서의 GC의 Pause time을 줄이는것.



### Garbage First Collector - G1GC

CMS에 비해 Pause Time이 개선되었고 예측 가능한게 장점이다.

G1GC는 Young, old 구분을 업생고 전체 힙을 1MB단위 region으로 잰편한다.

가비지만 꽉찬 리전별로 먼저 가비지 컬렉션을 수행한다. 

G1GC에서 객체가 Allocation되는 영역의 집합을 Young, Promotion되는 Region의 집합을 Old라고 한다.

![image-20240130224457363](./images//image-20240130224457363.png) 

작은 네모가 Region이며, 까만 네모는 Old Region, 하얀 네모는 Young Region이다

3은 Young에서 방금 Copy된 Survivor Region이다.

4는 방금 Promotion 되어 새로 Old가 된 Region이다.

Miner GC가 발생하면 Young Region을 대상으로 Reachable을 찾고, Survivor Region으로 카피한다.

Promotion의 대상 객체는 Old Region으로 카피한다.

기존 Young Region은 가비지로 간주해 Region 단위로 컬렉션한다.

Young Region GC가 끝나면 바로 Old region GC를 시작하는데, 힙 전체가 아닌 철저한 Region 단위이다. 

#### Garbage First Collector| Garbage First Collection

G1 Collector의 Garbage Collection은 4 단계, 세부적으로는 6 단계이다.

<img src="./images//image-20240130224722597.png" width = 700>

1. Young GC : Minor GC. 멀티쓰레드가 작업한다. 살아있는 객체는 Age에 맞게 Survivor 또는 Old로 Copy되며 기존 공간은 해지된다. 이후 새로운 객체가 할당되는 리전은 그 근처 비어있는 Region이 된다.

2. Concurrent Mark phase (mark -> remark): Old 영역 GC 시작.

![image-20240130224904570](./images//image-20240130224904570.png)

* Marking 단계 : Single Thread, 전체적으로 Concurrent, 이전 단계 때 변경된 정보 바탕으로 Initial Mark를 빠르게 수행한다.

- ﻿﻿Remarking 단계 : Suspend 있고 전체 Thread가 동시작업, 각 Region마다 Reachable Object의 Density를 계산, 그 후 Garbage Region은 다음 단계로 안 넘어가고 바로 해지된다.



# GC 튜닝

![image-20240130225131122](./images//image-20240130225131122.png)

* 튜닝 옵션들.

## 튜닝 필요성

일반적으로 다음과 같은 상황이라면 GC 옵션을 통한 튜닝이 필요하다고 볼 수 있다.

- ﻿-Xms 옵션과 -Xmx 옵션으로 Memory 크기 설정 없이 사용중이다.
- ﻿﻿JVM 옵션에 -Server 옵션이 설정되어 있지 않다.
- ﻿﻿시스템에 Timeout 같은 로그가 발생하면서 정상적인 트랜잭션 처리가 이루어지지 않는다.

1. 가능한 객체 생성을 줄이면 GC가 적다
2. String대신 StringBuffer, Builder 사용. 문자열을 변경할 경우 String은 객체 자체를 복사해서 값을 변경하므로 Heap에 객체가 계속 쌓이지만, StringBuffer, Builder는 내부적으로 복사가 이뤄지지 않고 객체의 값만 변경된다.
3. 로그를 최대한 적게 쌓는것이 좋다

## GC 튜닝 목적

시스템의 Suspend Time (Stop the word)를 줄이는것이 목적.

세부적으로 보면

1. Old 영역으로 넘어가는 객체 수 최소화
2. Full GC 실행시간 감소

## 객체 수 최소화의 중요성

객체 수를 줄이면 Old 영역으로 넘어가는 객체 수를 줄일 수 있기 때문에 Full GC가 발생하는 빈도를 많이 줄일 수 있다.

## Full GC Time 줄이기

1~2초 걸리면 오래걸린것이다. 

Old 영역의 크기를 줄이게 되면 OOM이 발생하거나 Full GC가 더 많이 발생한다.

반대로 늘리게 되면 Full GC 횟수는 줄지만 발생하면 실행시간이 오래걸리므로 Old 영역은 적절해야 한다.

## GC의 성능을 결정하는 옵션

* -Xms 옵션 - JVM 시작 시 할당할 초기 힙 메모리 크기를 설정
* -Xmx 옵션 - JVM이 사용할 수 있는 최대 힙 메모리 크기를 설정
* -XX:NewRaio 옵션 - Young Generation과 Old Generation 사이의 메모리 비율을 설정
  *  `-XX:NewRatio=3`은 Old Generation이 Young Generation의 세 배 크기가 되도록 설정
  * Young이 작으면 자주 마이너 GC 발생, 너무 크면 Old에 공간이 모자름

## GC 튜닝고정

1. jstat을 이용한 지표 모니터링

-verbosegc 옵션과 -Xloggc:<파일위치> 옵션을 사용하여 JVM에서 로그를 멀구 도록 설정한다. 

2. VisuamVM 사용, JConsole,MAT 등 사용

## GC 튜닝이 불필요한 상황

*  Minor GC의 처리 시간이 50ms 내외로 빠른 경우

* Minor GC 주기가 10초 내외로 빈번하지 않은 경우

* Full GC의 처리 시간이 보통 1초 이내로 빠른 경우

* Full GC 주기가 10분에 1 회 정도로 빈번하지 않은 경우

## GC 방식 선택

CMS가 다른 GC보다 빠르지만, 항상 빠른것은 아님.

Compaction (압축, 단편화 해제) 작업에 따라 항상 다르다.

GC와 운영중인 특성에 따라 적절한 GC를 선택해야 한다.

* **CMS의 속도**: CMS는 빠르고 중단 시간이 짧지만, 메모리 단편화와 CPU 사용량이 높은 문제가 있다.
* **Compaction (압축)**: CMS는 메모리를 압축하지 않아, 오래 실행될수록 단편화 문제가 발생할 수 있다. 반면, G1과 같은 다른 GC는 메모리를 주기적으로 압축하여 단편화를 방지
* 예를 들어, 낮은 중단 시간이 중요하다면 CMS나 ZGC가 좋은 선택일 수 있다.

즉

1. **저지연, 대응 시간이 중요한 실시간 시스템**:
   - **적절한 GC**: ZGC 또는 G1 GC
   - **이유**: ZGC는 매우 낮은 지연 시간을 제공하며 대용량 힙에서도 효과적입니다. G1 GC도 예측 가능한 중단 시간을 제공하여 실시간 성능에 적합합니다.
2. **대규모 힙을 사용하는 데이터 분석 및 처리 시스템**:
   - **적절한 GC**: G1 GC
   - **이유**: G1 GC는 대용량 힙을 관리하면서도 중단 시간을 줄일 수 있어, 대규모 데이터를 다루는 시스템에 적합합니다.
3. **CPU 사용량이 민감한 시스템**:
   - **적절한 GC**: Parallel GC
   - **이유**: Parallel GC는 여러 CPU 코어를 활용하여 빠르게 메모리를 정리할 수 있으나, 중단 시간이 길 수 있습니다. CPU 자원이 충분한 시스템에서 효과적입니다.



## Memory 크기와 GC 상관관계 

* Memory 크기가 크면 GC 발생 횟수는 줄어들고 GC 수행 시간은 증가한다.

* Memory 크기가 작으면 GC 수행 시간은 줄어들고 GC 발생 횟수는 증가한다.

정답은아니다. 트레이드오프다. 

어떤서버는 10GB 메모리여도 1~2초고, 일반적으로는 10GB 면 20~30초이상은 소요된다.

만약 Full GC 후 300MB 밖에 안되면 500MB이상 여유 있도록 공간을 여유롭게 하자. 



NewRatio도 고려해야 한다 (Young, Old의 비율)

-XX:NewRaio=1이면 1:1, 1GB 메모리라면 500MB:500MB

2면, 1:2, 즉 333: 666이 된다. 



# 2.5 GC 성능 테스트 - p90쪽까지 볼것 



1) 개요
2) Case 1
3) Case 2 / Case 2-1
4) Case 3
5) Case 4
6) 결론



# 2.6 GC 관련 장애 발생 유형(OOME) 및 분석 방법  91



## 1. OOME의 종류

JVM이 Heap Memory에 더 이상 Object를 할당할 수 없을 때 발생하는 오류이다. 

OOME 에러는 보통 아래와 같이 두 가지 유형으로 크게 구분할 수 있다.

- ﻿﻿Java.lang.OutOfMemoryError: Java heap space
- ﻿﻿Java.lang.OutOfMemoryError : PermGen space 



해결방법1.  -Xmx 옵션을 사용하여 힙메모리 증가 시키는 법이 있다. 그러나 GC Time의 증가를 동반한다.

2. 힙덤프 분석으로 많은 메모리를 사용하는 로직을 찾아 수정해야 한다.



### Java.lang.OutOfMemoryError : PermGen space 

Jvm 기동시 로딩되는 Class 또는 String 수가 많으면 OOM PermGen space의 원인이 된다.

해결방법 : JVM Option 튜닝



## OOME 분석 툴

1. VisuamVM -> 쓰레드모니터링, 메모리 모니터링, 쓰레드 덤프 및 힙 덤프
2. Jmap -> 힙 덤프
3. MAT -> 이클립스 기반 메모리 분석 툴 



# JVM Sychronization이란?

WAS에서는 동시사용자를 처리하기 위해 수십~수백개의 쓰레드를 사용하는데, 두개이상의 쓰레드가 같은 자원을 이용하면 필연적으로 경합이 발생한다. 때로는 데드락도 발생한다. 

## Thread 동기화

Java에서는 Morinot를 이용해 쓰레드를 동기화 한다. 모든 자바 객체는 하나의 Monitor만 가지며 하나의 쓰레드만 소유할 수 있다.

자바에서 Monitor를 점유하는 유일한 방법은 Synchronized 키워드를 사용하는 것이다. 

## Mutual Exclusion과 CriticalSection

Heap에는 객체의 멤버변수가 있는데, JVM은 해당 객체와 클래스를 ObjectLock을 사용해 보호한다.

Object Lock은 한번에 한 쓰레드만 객체를 사용하게끔 내부적으로 Mutex등을 활용하다. 

이 Synchronization은 DBMS의 Lock과 다르다. DB는 SELECT(락 제외)는 락을 안걸지만, 쓰기는 발생할 수 있다. 

그러나 자바는 Synchronization 영역에 들어가면 무조건 동기화를 수행한다. 

![image-20240203010359352](./images//image-20240203010359352.png)

Java Synchronization 성능이 왜 안좋을까?

* 용어부터 정리
  * **EntrySet**: 일반적으로 "EntrySet"은 공유 자원에 접근을 시도하는 스레드들의 집합. 자원을 기다리고 있는 상태
  * **WaitSet**: "WaitSet"은 자원을 획득한 후에 특정 조건이 만족될 때까지 대기하고 있는 스레드들의 집합을 의미. 대기 큐를 "WaitSet"이라고 할 수 있다.
* 쓰레드는 wait()을 호출하면 조건 변수에 대해 대기 상태로 진입되고 블락이된다. 대기 큐(wait set)으로 이동한다. 

 `synchronized` 블록이나 메소드를 사용할 때, 스레드는 이 락을 획득해야 하고 이 락은 오직 한 스레드만이 가질 수 있다. 다른 스레드가 락을 가지고 있으면, 획득을 시도하는 스레드는 블록되거나 대기 상태가 되기 때문이다.

Entry Set에서 획득을 시도하지만 얻지 못한다면 계속 대기하게 된다. 

wait set에 들어온 쓰레드가 임계구역을 벗어나는 방법을 monitor를 다시 획득해 Lock을 놓고 나가는 방법 뿐이다. 

때문에 성능이 좋지않다.

이 Monitor를 이용하거나 OS의 자원인 mutex등을 이용한 Lock을 Hevy Lock이라고 하며, Atomoic 연산을 이용한 락을 Light-weight Lock 이라고 한다. 

## Thread 상태

<img src="./images//image-20240203011625939.png" width = 650>

* Thread의 상태는 Thread 클래스 내부에 State라는 Enum으로 선언되어 있다.

- ﻿﻿NEW: Thread가 생성되었지만 아직 실행되지 않은 상태
- ﻿﻿RUNNABLE: 현재 CPU를 점유하고 작업을 수행 중인 상태. 운영체제의 자원 분배로 인해 WAITING 상태가 될 수도 있다.
- ﻿﻿BLOCKED: Monitor를 획득하기 위해 다른 Thread가 Lock을 해제하기를 기다리는 상태
- ﻿﻿WAITING: wait() , join() Method, park() Method 등 을 이용해 대기하고 있는 상태
- ﻿﻿TIMED_WAITING: sleep(), wait(), join(), park() Method등을 이용해 대기하고 있는 상태. 
  - WAITING 상태와의 차이점은 Method의 인수로 최대 대기 시간을 명시 할 수 있어 외부적인 변화뿐만 아니라 시간에 의해서도 WAITING 상태가 해제될 수 있다는 것이다.

## Thread 종류

데몬 쓰레드와 논데몬 쓰레드로 나눌 수 있다. 데몬 쓰레드는 다른 `비데몬(논데몬)쓰레드가 없다면 동작을 중지한다.`

main()으로 실행되는 쓰레드는 비데몬 쓰레드로 생성되고, 이 쓰레드가 중지하면 다른 데몬 쓰레드도 동작을 중지한다.

- ﻿﻿`VM Background Thread`: Compile, Optimization, Garbage Collection 등 JVM 내부의 일을 수
   행하는 Background Thread 들이다.
- `Main Thread`: main(String[] args) Method를 실행하는 Thread로 사용자가 명시적으로 Thread를 수행하지 않더라도 JVM은 하나의 Main Thread를 생성해서 애플리케이션을 구동한다.
  -  Hot Spot JVM에서는 VM Thread라는 이름이 부여된다.
- ﻿`﻿User Thread`: 사용자에 의해 명시적으로 생성된 Thread 들이다. Javalang.Thread를 상속
   (extends)받거나, Javalang, Runnable 인터페이스를 구현(implements)함으로써 User Thread 를 생성할 수 있다.

## Thread Dump

쓰레드 덤프 생성방법

* kill -3 [pid]
* jstack [pid]

## Thread Dump를 통한 Thread 동기화 문제 해결의 실 사례

실제 운영 환경에서 성능 문제가 발생한 경우에 추출한 것으로 Thread Dump를 분석한 결과 많은 수의 Worker Thread들이 다음과 같이 블로킹되어 있었다.

<img src="./images//image-20240203012852010.png" width = 750>

* 컨넥션을 얻는 과정에서 Synchronized에 의한 스레드 블록킹 발생.
* Connection Pool에서 얻는 과정에서 Thread 경합이 발생한 것으로, Pool이 완전히 소진되었고 새로운 DB Request에 새 Connection을 맺는 과정에서 성능 저하가 발생. 

이문제를 해결하는 방법은 커넥션 풀의 초기 커넥션 수와 최대 커넥션 수를 키운다.

* 만일, 실제 DB Request는 적은데 발생한다면 Connection 누수. 



# 도구를 이용한 성능 분석

JDK 내장 분석 도구

## Thread Dump와 Stack Trace 정보.

각각 스레드는 Stack이라는 고유 공간을 할당받고, 메소드 호출정보가 순서대로 저장이 되고 지역 변수정보가 저장된다.

동적으로 생성되는 객체에 대한 정보는 Heap 영역에 저장된다. 

CallStack은 메소드가 호출된 순서대로 각 스레드마다 순서대로 저장되며, **가장 마지막에 호출된 스레드가 가장 상위에 저장되는 구조**이다. 

## Heap Dump 정보

**Heap Dump란?**

Java Heap 메모리에 생성된 객체에 대한 정보를 특정 시점에 스냅샷 뜬 정보.

생성된 객체와 객체의 필드 정보를 제공하며, 스레드 Call Stack정보도 포함된다.

* 어떤 스레드가 어떤 객체를 사용하는지에 대한 정보도 파악 가능하다.

**Heap Dump 파일은 어디에 사용될까**

OOM 에러가 발생한원인 분석이 가능하고, 가장 메모리를 많이 사용하는 객체가 무엇인지 알 수 있다.

OOM의 경우 보통 컬렉션 객체가 너무 많이 들고있고, 사용하지 않는데도 참조를 유지해서 GC가 되지 않아 발생하는 경우가 많다.



Heap Dump 파일의 HPROF 2진 형식 데이터는 모든 데이터 뿐만 아니라 원시 데이터와 스레드 세부사항도 들어있다. 

### Heap Dump 파일 생성 방법

Heap Dump 파일 생성은 아래와 같이 생성 가능하다.

```shell
jcmd <pid> GC.heap_dump filename=<filename> 
jmap -dump: live, format=b, file=heapdump.hprof <pid>
```

**힙덤프 수행시 GC 수행 후 메모리 SnapShot을 수행하기 때문에 사용하지 않는 객체는 모두 GC에 의해 사라지고 Live 객체에 대한 정보만 Snapshot에 저장이 된다.**

* jmap 옵션에` live 옵션을 빼면 GC 수행 전의 스냅샷을 뜰 수 있다.`

Heap Dump시 애플리케이션이 중단되므로 런타임에는 주의하자. 

## 객체 참조,  GC와 메모리 누수

GC root로부터 참조되면 GC 대상이 아니고, 참조가 끊기면 GC 대상이다.

보통 메모리 누수의 이유는 사용되지 않는객체가 Gc root로부터 참조가 남아있어 유지되기 때문에 발생한다.

Gc Root 목록

* System Class Loader에 의해 로딩된 클래스
* 스레드 stack 내의 local vairable 또는 parameter
* Monitor로 사용된 객체 
* 정적 변수 
* JNI 메소드 및 전역 JNI 메소드

가비지 컬렉터는 Gc root 객체들로 부터 시작해서 참조되지 않는 객체들을 GC하여 수거한다

![image-20240404231550555](./images//image-20240404231550555.png)

## Java Flight Recording 기능 사용하기

JFR은 실행중인 자바 애플리케이션 진단 및 프로파일링 도구이다.

CPU 프로파일링은 각 자바 메소드에서 소비되는 상대적 CPU 시간 양과 모든 시스템 함수를 계산한다.

heap 프로파일링은 얼마나 힙 메모리를 사용하는지 어떤 객체가 할당되어있는지 진단하고 볼수있다.

JFR프로파일링은 운영중인 시스템에 부하를 거의 주지 않기 때문에 운영중 시스템에 프로파일링 수행시 효과적으로 사용될 수 있다.

JFR의 4가지 수집 event

* instant event : 즉시 발행, 발생 즉시 로깅
* duration event : 시작시간 종료시간 로깅. event 종료시 로깅
* timed event : 특정 임계값이 초과된 이벤트만 기록
* sample event : 일정한 시간 간격으로 활동 정보를 수집하기 위해 로깅



## GC 메모리 분석 기능 사용 - 빠르게 메모리 파악

```
jcmd pid GC.class_historam > pic_ClassHistogram.txt
// or
jmap -histo pid 
```

FULL GC를 하고 Heap 메모리 점유율을 보여준다

* jmap 같은경우 live 명령어를 빼면 죽은객체 포함해서 정보를 표시한다.

IC 같은 Java 시그너처 형태는 아래의 값을 의미한다.

* [Z = boolean 배열

* [B = byte 배열

* [s= short 배열

* [I= int 배열

* L = long 배열

* [F= float 배열

* [D = double 배열

* [C= char 배열

* [L= any non-primitives(Object) Java객체 배열

![image-20240404233909677](./images//image-20240404233909677.png)

힙덤프 파일은 IBM HeapAnalyzer, VisualVM 등으로 분석 가능하다.

## Managerment Agent( JMX)

JMX : Java 애플리케이션 관리하고 모니터링 하기 위한 도구.

기본적으로 제공하며 하드웨어 등에 대한 인터페이스도 제공하여 외부에서 통계 및 수집할 수 있다.

### JMX 용어

- ﻿Manageable Resource : 관리대상이 되는 리소스(애플리케이션, 장비)
- ﻿﻿MBean : Managed bean의 약자. Manageble Resource에 대한 접근 및 조작에 대한 interface를 제공
- ﻿MBean Server : MBean을 등록. MBean Server를 통해 JHX 관리 Application에 정보 제공
- ﻿Management Application : JMX 활용 App을 관리하는 애플리케이션. Protocol Adapter와 Connetor을
   통해 외부 원격 management application에 접근을 제공한다. (JConsole, INC 등)

### JMX 아키텍처

관리 대상 리소스에 MBean을 설치하고 MBean Server(JMX Agent)에 등록되어 관리된다.

MBean Server가 외부 Managent Application으로 연결되어 Managerment Application을 이용해서 ManageableResource를 관리 및 감시한다.

![image-20240404234245374](./images//image-20240404234245374.png)

JMX 아키텍처를 간단히 표현하면 아래와 같은 연결 구조를 가지고 있다.

* Manageable Resource(관리 대상 Java App) <- MBean <-MBean Server <-Management Application

JMX 관리기능 on/off 방법

```
jcmd <pid> MAnagermentAgent.start : 원격 JMX 켜기
jcmd pid ManagermentAgent.start_local : 로컬 JMX 켜기 
jcm pid ManagermentAgent.stop : JMX 끄기 
```

## JCM 도구 vs 다른 도구 비교

![image-20240404234419844](./images//image-20240404234419844.png)



# Java Misson Control 도구의 활용



## 실시간 모니터링

JMC를 기동하고 원격 JVM의 ip, port로 접속할 수 있다.

<img src="./images//image-20240404234703216.png" width =450 height = 450>

Memory 탭에서 JVM 메모리 영역의 모든 상황을 볼 수 있다.

Threads 탭에서 실행중인 스레드와 Stack 정보와 아래 정보를 제공한다.

* 스레드 명

* 스레드 상태 정보 : RUNNABLE, TIMED_WAITING, WAITING 등

* Lock이 걸린 인스턴스 이름

*  스레드가 BIoCK된 횟수

* 스레드 실행 후 할당된 메모리 크기

* 스레드별 CPU 사용률

### GC 관련 JVM 옵션

![image-20240404235052982](./images//image-20240404235052982.png)

### TLAB을 이용한 성능향상 (Thread Local Allocation Buffer)

TLAB는 Thread Local Allocation Buffer의 약자로, Heap의 Young Generation 중 Eden 영역에 여러 스레드가 동시에 메모리 할당 시에 스레드 간 경합에 의한 성능감소 문제 해결을 위해 `각각의 스레드 별로 메모리 영역을 개별로 할당하여 성능향상을 도모`한 Heap 메모리 할당 방식이다.

TLAB에 할당된 객체는 성능이 향상되지만 경합이 발생하는 TLAB 바깥 영역에 객체가 할당이 되면 성능이 떨어지게 된다. 객체 할당 시에 TLAB에 충분한 크기의 공간이 없는 경우에 TLAB 바 깥 영역에 객체가 할당될 수 있다.

`TLAB의 크기를 옵션을 통해 적절히 조절하여 성능 향상을 구현할 수 있다`. 큰 객체를 많이 할 당하는 어플리케이션, 에덴의 크기에 비해 상대적으로 스레드의 개수가 많은 어플리케이션은 TLAB 튜닝으로 상당한 이익을 얻을 수 있다. TLAB 튜닝은 JER 모니터링을 통해 TLAB 바깥에 얼마나 많은 메모리가 할당되는지 확인하고, 객체들의 평균, 최대 크기를 확인한 다음에 TLAB 의 크기를 평균 객체들의 크기로, 또는 조금 더 크게 조절하여 튜닝한다.

**TLAB의 옵션**

* -XX:-UseTLAB default: -XX: +UseTLAB)
  * TLAB의 사용을 중단한다. TLAB를 사용하면 성능이 향상된다. default는 사용하는 것이다.

* -XX: TLABSize=N (default: 0)
  * TLAB의 크기를 지정한다. 0으로 지정되면 Eden 크기에따라 자동으로 크기가 조정된다. 
  * 즉, Eden 크기가 증가하면 TLAB의 크기도 증가한다. GC가 발생할 때마다 Eden 크기가 변경되므로 TLAB의 크기도 변경된다. GC가 발생할 때마다 크기가 변경되는 것을 방지하려면 -XX:-ResizeTLAB을 추가해야한다. TLAB 을 튜닝할 때는 크기가 계속 변경되면 하기 힘드므로, 추가하는 것이 좋다.

- ﻿XX: TLABWasteTargetPercent=N(default: 1)
   새로 할당될 객체의 크기가 (TLAB 크기) * (이 옵션에 지정된 퍼센트값)• 보다 작은 경우에는 TLAB 바깥 영역에 할당하고, 큰 경우에는 TLAB를 버리고 새로 시작한다.
- ﻿XX: TLABWasteIncrement=N ( default :4 )
   TLAB 바깥에 할당이 이루어 질때마다 TLABWasteTargetPercent의 값을 N씩 증가시킨다. TLAB가 버려질 가능성을 점점높혀서 객체가 7속 TLAB 바깥에 할당되는 것을 방지한다.

# 성능분석 도구들 비교

![image-20240405000357461](./images//image-20240405000357461.png)


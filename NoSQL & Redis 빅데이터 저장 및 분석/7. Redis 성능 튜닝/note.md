# 7 Redis 성능 튜닝

[toc]





# 성능 튜닝 방법론

준비단계 -> 튜닝 / 분석단계 - 결과단계로 이어진다

**1. 준비단계(Step-1)**

*  과학적이고 객관적인 원인 분석이 되기 위해서 Redis Server에서 제공하는 튜닝 툴을 활용하고, 현상 수집된 결과를 이용하여 성능 지연 문제를 유발시킨 원인을 찾아내고 향후 어떻게 문제를 해결할 것인지에 대해 튜닝 계획을 수립해야 한다

**2. 튜닝/분석단계(Step-2)**

*  Step-1 단계에서 원인 분석 결과를 기반으로 실제 튜닝을 수행하는 단계

* 원인 분석 결과가 명확하지 않은 경우, Server 튜닝, Query 튜닝, Design 튜닝 순으로 진행하는 것이 효과적임

*  `한번 발생했던 성능 이슈는 문제 해결 후 재발생 가능성이 높기 때문에 문서화를 해두면 불필요한 비용과 시간을 현저히 줄어들 수 있다`

**3. 결과단계(Step-3)**

*  튜닝 전 수집된 분석 결과와 튜닝 후 분석 결괄르 비교하고 얼마나 개선되었고, 성능 목표에 도달했는지 여부를 객관적이고 과학적인 방법으로 평가하는 단계

모든 작업이 수행되면 최종 보고서를 작성하고 튜닝 작업을 마치게 된다

# 성능 튜닝 포인트

system, design, query ,server 를 튜닝할 수 있다.

**1. System 튜닝**

*  Redis는 다른 NoSQL 제품에 비해 상대적으로 시스템 환경(하드웨어 네트워크)이 성능에 미치는 영향이 클 수 밖에 없다

*  시스템 환경이 Redis Server에 미치는 영향에 의해 성능 지연 문제가 발생하는데, 그 원인을 분석하고 적절한 대응 조치 시스템 튜닝이라 한다. 

- Redis 튜닝에서 System 튜닝이 차지하는 비율은 매우 높다

**2. Query 튜닝**

*  개발자가 작성한 Query 중에서 성능 지연 문제를 유발시키는 문장들이 존재할 수 있으며, Query 문장의 비 최적화, 인덱스 생성 여부, 잘못된 인덱스 타입의 선택 등으로 성능 지연 문제가 발생할 수 있는데 이에 대한 원인 분석과 대응 조치를 말한다 → 문장(Statement) 튜닝

* 관계형 DBMS와 달리 Redis Server에는 System 튜닝과 Server 튜닝에 비해 중요성은 조금 떨어진다

**3. Server 튜닝**

*  Redis Server의 아키텍처는 메모리 영역, 프로세스 영역, 파일 영역으로 구성된다

*  각 영역은 가장 최적화된 환경으로 설치, 구축 운영되어야 하는데, 구축된 비즈니스 환경, 설계된 데이터 저장 구조에 최적화되지 못한 경우, 성능 지연 문제가 발생할 수 있다

**4. Design 튜닝**

* 데이터가 저장되는 오브젝트의 논리적 설계(테이블 구조, 인덱스 구조) 상에 구조적인 문제로 인해 발생하는 성능 튜닝 영역을 말한다

# 시스템 튜닝

`레디스 성능 최적화를 위해 고려해야 할 포인트 중 가장 중요한 성능 포인트는 시스템 튜닝이다.`

인메모리 기반 데이터 저장 구조이기 때문에 무엇보다 메모리 영역의 크기와 설계가 성능에 직관된다.

## **1. 블록 사이즈**

**1) Redis Server를 설치하기 전 가장 먼저 고려해야할 점은 디스크 장치의 블록 사이즈를 어떤 값으로 설정해야할지를 결정해야 함**

(2k, 4k, 8k, 16k, 32k, 64k....)

포맷되는 디스크 블록 사이즈는 향후 Redis Server 전용 메모리 영역의 블록 사이즈로 결정되며 aof, rdb 파일의 블록 사이즈를 결정하는 기준이 된다

**운영체제의 종류와 버전에 따라 블록 사이즈의 기본값은 조금 다르지만 8K 또는 16K 이상 값의 설정을 권장한다**

그러나 이 결정은 해당 서버에 설치된 리눅스 서버의 용도와 데이터 저장량 등을 고려하여 결정해야 한다.

진짜 예제로 생각해보면,

많은 사용자가 동시에 입력/수정/삭제/조회 위주의 작업을 수행할 경우라면 기본값보다 한 단계 낮은 값을 권장

배치 작업, 소수의 사용자가 조회 위주의 데이터를 처리하는 용도라면 기본값보다 두배를 권장



## 2. NUMA & Transparent Huge Pages(THP) 설정 해제

Redis Server는 메모리 영역에 대한 할당과 운영, 관리와 관련한 다양한 메커니즘을 자체적으로 제공한다

리눅스 서버의 경우, 시스템 RAM 메모리 영역을 효율적으로 운영 관리를 위한 자체적인 메커니즘인 NUMA(Non-Uniform Memory Access)와 THP(Transparent Huga Page)를 제공한다.

리눅스 서버에서 제공하는 메커니즘과 레디스 서버에서 제공하는 매커니즘이 겹치면 레디스 메커니즘이 정상적으로 작동하지 못하기 때문에 설치 단계에서부터 충분히 예견되는 문제점들이기 때문에 설정 해제를 권장

```sh
$ cat /sys/kernel/mm/transparent_hugepage/enabled
[always] madvise never

$ echo never > /sys/kernel/mm/transparent_hugepage/enabled
$ cat /sys/kernel/mm/transparent_hugepage/enabled
always madvise [never]

$ vi /etc/rc.local
echo never > /sys/kernel/mm/transparent_hugepage/enabled
```

## 3. Client Keep Alive Time 설정

클라이언트가 Redis Server에 접속한 후 일정한 타임아웃 시간이 지나고 나면 해당 세션은 종료되고, 재접속을 요구하는 경우에는 즉시 접속할 수 없는 상태일 때 대기시간이 발생한다.

이 문제점을 해소하기 위해서는 클라이언트가 일정 시간 동안 작업을 수행하지 않더라도 세션을 일정시간동안 유지시켜 재 접속을 피하고 대기시간을 줄이는 것이다

```sh
$ vi /etc/sysctl.conf
net.ipv4.tcp_keepalive_time = 7200
```

## 4. Key Expire를 통한 대기시간 최소화

Redis 인스턴스 영역에는 수많은 Key와 Value 들이 저장되어 있다.

클라이언트에 의해 실제로 Access되고 사용되는 key도 있겠지만, 오랜시간동안 참조되지 않는 Key도존재한다.

이것을 해소하기 위해서 Redis Server는 Key를  만료(expire)시킬 수 있는 기능을 제공한다

### Redis Server에서 Key를 만료시킬 수 있는 기능

**1. LazyFree 파라미터**

더 이상 참조되지 않는 Key를 메모리로 부터 제거하는 방법

**2. ACTIVE_EXPIRE_CYCLE_LOOKUPS_PER_LOOP 기능**

\- Redis Server가 내장하고 있는 ACTIVE_EXPIRE_CYCLE_LOOKUPS_PER_LOOP 기능에 의해 100millisecond 마다(초당 10회) 만료된 Key를 자동 삭제해주는 방법

\- 기본값은 20으로, 초당 200개의 Key를 만료시켜 제거해준다

## 5. 최적화 시스템 환경 설정

**시스템 환경 설정과 관련하여 고려되어야 할 기타 내용**

```bash
# /etc/sysctl.conf 파일을 편집기(vi)로 열어 커널 설정을 변경합니다.
$ vi /etc/sysctl.conf
  # 파일 내에 다음을 추가하여 메모리 오버커밋을 허용합니다. 이는 프로세스가 요청한 것보다 더 많은 메모리를 커널에게 요청할 수 있게 해줍니다.
  vm.overcommit_memory = 1

# 현재 시스템의 net.core.somaxconn 값을 조회합니다. 이 값은 소켓의 최대 연결 대기열 크기를 설정합니다.
$ sysctl -a | grep somaxconn
  net.core.somaxconn = 128

# net.core.somaxconn 값을 65535로 설정하여, 소켓의 최대 연결 대기열 크기를 늘립니다.
$ sysctl -w net.core.somaxconn=65535
  net.core.somaxconn = 65535

# /proc/sys/net/core/somaxconn 파일을 읽어, 현재 설정된 net.core.somaxconn 값을 확인합니다. 이 값은 위에서 설정한 65535가 되어야 합니다.
$ cat /proc/sys/net/core/somaxconn
  65535

```



## 7.3.1 대기시간 모니터링

** 시스템 문제로 인해 발생하는 대기 시간 분석 방법**

**1) OS 커널, HyperVisor에서 발생하는 Intrinsic latency(고유 대기 시간)을 모니터링하는 방법**

```bash
$ redis-cli --intrinsic-latency 100          # 100초 내에 발생하는 latency 분석

$ redis-cli --intrinsic-latency 200          # 200초 내에 발생하는 latency 분석
```

### **2) 네트워크 문제로 인해 발생하는 대기 시간에 대한 원인 분석 및 대응 방안**

**edis Server 내에서 데이터 처리에 소요되는 시간은 대부분 100 Microsecond 범위 내에서 수행할 수 있지만,**

**클라이언트에서 TCP/IP 또는 Linux Domain-Socket을 통해 서버에 접속하여 데이터를 전송하는 데 30~200 microsecond 정도 소요된다**

Redis Server에서 데이터를 처리하는 시간보다 클라이언트에서 서버로 접속하여 문장을 전송하고 서버에서 처리한 다음 그 결과를 다시 전달받는데 소요되는 시간이 더 많이 발생한다.

때문에 **하나의 서버로 시스템을 구축하는 것보다 여러 대의 물리적 서버를 분리하여 구축하는 것이 성능 개선에 도움을 준다**

**웹 환경의 클라이언트로부터 서버에 접속하다 보면 일정 시간 동안 작업을 중지했을 때 해당 세션은 유휴 상태가 되고, 접속이 해제되게 됨**

\- 다시 작업이 재개 되었을 때, 불필요한 재접속이 이루어지게 되는데 이때 대기 시간이 발생하게 되며 이러한 현상을 최소화하기 위해 Client keepalive Time을 적절하게 설정하여 불필요한 접속을 피해야 함

\- 기본값 : 7200

```sh
$ vi /etc/sysctl.conf
net.ipv4.tcp_keepalive_time = 7200
```

**Redis는 단일 스레드 작업이 수행되도록 설계**되었으므로, 배치 작업, 분석 작업들이 같은 CPU 코어에서 작업되지 않도록 분리해야 한다.

# Slow-Query 튜닝

## 1. SlowLog 수집 및 분석

**Redis Server는 2.2.12버전부터 사용자가 실행한 Query 문장 중에 성능 지연 문제가 발생한 문장을 자동으로 수집한 후 제공한다**

\- 수집 결과에서 실행시간은 해당 Query가 서버에서 실제 실행된 시간 정보를 나타낸다

\- slowlog 명령어를 통해 확인할 수 있다

```
slowlog
```

```sh
> set 317260 JMJOO
> get 100
>
> slowlog ?
(error) ERR Unknown SLOWLOG subcommand or wrong # of args. Try GET, RESET, LEN.
>
> slowlog get 2
1) 1) (integer) 0
   2) (integer) 1533196205
   3) (integer) 10260
   4) 1) "GET"
      2) "317260"
   5) "127.0.0.1:59266"
   6) "PC-123"
> slowlog len
(integer) 1                 # 현재 저장되어 있는 Slow Query 개수
>
> slowlog reset             # 저장되어 있는 모든 Slow Query를 제거하고 초기화
OK
```

**분석 결과 출력 항목 설명** 

```sh
> slowlog get 2
1) 1) (integer) 0            # Slow Query ID(식별자)
   2) (integer) 1533196205   # 운영체제 Timestamp
   3) (integer) 10260        # 실행 시간(millisecond)
   4) 1) "GET"               # 실행된 명령어를 저장하는 배열
      2) "317260"            # Key
   5) "127.0.0.1:59266"      # 클라이언트 IP 주소 및 포트(4.0버전부터)
   6) "PC-123"               # CLIENT SETNAME 명령을 통해 클라이언트명을 설정한 경우 출력(4.0버전부터)
```

## 2. 논리적 데이터베이스 설계를 통한 쿼리 성능 개선 방법

하나의 Redis Server는 기본적으로 Database 0 ~ Database 16까지 17개의 논리적 데이터베이스를 생성할 수 있으며 

기본 설치 후 접속하면 Database 0에 데이터를 저장할 수 있다.

**논리적 데이터베이스를 여러 개 설계해야 하는 이유**

\- 데이터의 안전한 저장 및 관리를 위해 기본적으로 분리 저장을 원칙으로 해야 함(Striping)

\- 데이터의 백업 및 복구 측면에서 하나의 DB로 생성하는 것보다 여러 개의 DB로 분리 생성하는 것이 유리함

\- 분산 Database Lock을 지원하기 때문에 보다 세분화된 DB 설계를 통해 성능을 개선시킬 수 있음



# 서버 튜닝

## 7.5.1 스와핑(Swaping) 모니터링 & 대응방안

**1. 스와핑(Swaping)**

\- Redis Server는 사용 가능한 메모리가 부족한 경우, 메모리에 저장된 데이터 일부를 디스크로 저장하거나 또는 디스크에 저장된 데이터를 메모리로 적재하는 경우가 발생하는 것을 말한다

\- 스와핑이 빈번하게 발생하고 발생량이 증가한다면 이와 같은작업을 수행하기 위해 불필요한 시간이 소요됨으로써 Redis Server의 성능 지연 문제가 발생할 수 있다

**2. 스와핑이 발생하는 경우**

\- Redis 인스턴스에 저장되어 있는 Data-Sets 또는 일부가 클라이언트에 의해 더 이상 참조되지 않는  유휴상태일 때 OS 커널에 의해 스와핑될 수 있다

\- Redis 프로세스 일부는 aof 또는 rdb 파일을 디스크에 저장하기 위해 작동되는데, 이 경우 스와핑이 발생할 수 있다

**3. Redis Server에서 모니터링하는 방법**

스와핑이 발생하는지, 얼마나 자주, 얼마나 많은 양의 데이터가 발생하는 지 모니터링하는 방법

```sh
$ redis-cli info | grep process_id    # Redis Server의 프로세스 ID 정보 분석

$ cd /proc/{process_id}                       # Redis Server에서 스와핑 발생여부 분석

$ cat smaps | grep 'Swap:'
Size:               1244 KB
Swap:                  0 KB
Size:               8192 KB                   # 스와핑 발생
Swap:                  0 KB
Szie:              19236 KB                   # 스와핑 발생

$ vmstat 1                                    # 스와핑발생 여부를 vmstat를 통해 확인
procs ------memory-------- ---swap-- --io--- -system -----cpu----
 r  b     swpd   free   buff   cache  si   so    bi    bo     in    cs   us   sy   id  wa
...
# si, so 컬럼이 나타내는 값을 통해 스와핑 발생 여부 확인
```

**4. 과도한 스와핑이 발생하는 경우에 최소화하기 위한 대응 방안**

**1) Redis 인스턴스 크기를 충분히 할당해야 함**

```sh
$ vi redis.conf 
maxmemory 100000000         # Redis 인스턴스 크기를 더 늘려줌
```

**Redis Server가 독립적으로 설치 운영되는 경우**

전체 RAM 크기의 약 90%까지 사용할 수 있도록 maxmemory 크기 설정을 권장함

**다양한 S/W가 함께 설치되어 운영되는 경우**

해당 S/W 업무 비중을 감안하여 결정하는 것이 바람직 함

예) Redis Server와 운영체계의 업무적 비중이 6:4 정도 된다면 RAM 크키의 60%를 Redis Server에 할당

## 7.5.2 AOF 파일에서 발생하는 디스크 IO 문제에 대한 대응 방안

**AOF 기능은 과도한 쓰기 작업이 발생할 때 성능 지연 문제가 발생함**

성능 지연 문제를 피하기 위해 관련 파라미터를 적절하게 설정해야 한다

```sh
$ vi redis.conf

appendonly yes
appendfilename                "appnedonly.aof"
auto-aof-rewrite-percentage   100
auto-aof-rewrite-min-size     64mb
appendfsync                   no    # no 또는 everysec 또는 always 중 설정
no-appendfsync-on-rewrite     no
```

**appendfsync 파라미터**

* Redis 인스턴스 상에 데이터를 AOF 파일로 내려쓰기하는 시점을 언제 수행할 것인지를 설정

* 기본값은 no로, everysec(1초), always 중에 선택할 수 있다

 어떤 값을 선택할지는 Redis Server의 업무 용도와 처리하는 데이터의 중요도에 따라 적절한 값을 결정해야 한다

**① everysec**

*  1초마다 쓰기 작업을 수행하게 됨

*  데이터 유실이 없어야 하고, 중요도가 높은 비즈니스 영역에 사용할 경우 권장

**② always**

*  실시간으로 지속적인 쓰기 작업이 수행됨

*  실시간 쓰기 작업을 통해 메모리 상의 중요 데이터를 디스크에 저장할 수 있지만 이로 인한 성능 지연 문제는 피할 수 없음

*  과도한 디스크 IO가 발생할 가능성이 높아 Redis Server 전체의 성능을 지연시킬 가능성이 매우 높음

앵간해선 everysec로 설정할것을 권장한다.

## 7.5.3 Scale-Out을 통한 분산 서버 구축 방안

하나의 Redis Server로 더 이상 성능을 기대할 수 없는 경우

새로운 분산 서버를 추가 할당하여 Scale-Out하는 것이 효과적이며, 시스템 효율성을 극대화 시킬 수 있다.

## 7.5.4  손상된 메모리 영역에 대한 충분한 테스트와 검증을 통한 안정화 방안

**Redis Server에게 할당된 메모리 영역에 대한 손상 여부 확인 방법**

redis-cli.exe에서 제공한 --test-memory 옵션을 사용함

```sh
$ ../src/redis-server --test-memory 2048    # 테스트할 RAM 크기 지정
```

## 7.5.5 Redis GDB(Gnu Debugger) 가이드를 이용한 버그 수집 및 분석을 통한 안정화 방안

**1) GDB 유틸리티의 주요 특징**

\- 현재 실행 중이 Redis Server 인스턴스에 연결하여 버그가 발생한 시점에 로그 메시지를 확인할 수 있음

\- 코어(core) 파일에 로그 메시지를 저장할 수 있고, 이를 Redis 개발팀으로 메일로 전송해주면 관려 내용을 분석한 후 조치를 받을 수 있음

\- GDB 유틸리티를 사용하는 것이 Redis Server의 성능 지연 문제를 유발시키지 않기 때문에 안전하게 사용할 수 있음

```sh
<터미널 1>
$ redis-cli info | grep process_id          # root 계정으로 실행

$ gcb redis-server {process_id}
(gcb) continue
Continuing.                                 # 디버깅 준비가 되었으면 Continue 입력

<터미널 2>
$ ../src/redis-cli -p 5001
127.0.0.1:5001> DEBUG SEGFAULT             # 강제로 장애 불량 충돌 명령어 실행

<터미널 1>
(gcb) continue
Continuing.

Program received signal SIGSEGV, Segmentation fault.  # 장애 불량 충돌 메시지 출력
debugCommand (c=0x7f6348c5bfc0) at debug.c:315
315               *((char*)-1) = 'x';
(gdb)

(gdb) BT                                   # BackTrace 명령어는 불량 충돌 전체 상태를 추적함
...
(gcb)
(gdb) info registers                      # 프로세스 레지스터 전체를 Dump해줌
...

(gdb) gcore                               # 프로세스의 모든 정보를 dump 파일로 저장해 줌
Saved corefile core.7072
(gdb)
Control-z

$ ls core.7072
core.7072                                 # 결과 코어 파일
```

**Redis 개발팀으로 전송할 정보**

\- 분석 및 수집된 코어 파일

\- 사용 중인 Redis 실행 파일

\- bt 명령에 의해 생성된 스택 추적과 레지스터 덤프 데이터

\- gdb로 생성한 코어 파일

\- 운영체제 및 GCC 버전, 사용 중인 Redis 버전에 대한 정보
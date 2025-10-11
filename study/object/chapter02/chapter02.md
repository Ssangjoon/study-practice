# CHAPTER 02 객체지향 프로그래밍

## 자율적인 객체
1. 객체는 상태(state)와 행동(behavior)을 함께 가지는 복합적인 존재다.
2. 객체는 스스로 판단하고 행동하는 자율적인 존재다.

### 객체의 외부와 내부
- 설계가 필요한 이유는 변경을 관리하기 위해서이다.
- `캡슐화`와 `접근 제어`는 객체를 두 부분으로 나눈다.
    - `퍼블릭 인터페이스(public interface)` : 외부에서 접근 가능한 부분
    - `구현(implementation)` : 외부에서는 접근 불가능하고 오직 내부에서 만 접근 가능한 부분
- 프로그래머의 역할을 구분할 수 있다.
    - `클래스 작성자` : 새로운 데이터 타입을 추가한다. 클라이언트 프로그래머에게 필요한 부분만 공개하고 나머지는 꽁꽁 숨겨야 한다
    - `클라이언트 프로그래머` : 클래스 작성자가 추가한 데이터 타입을 사용하여 객체 내부를 구현한다.
- 객체의 외부와 내부를 구분하면, 클라이언트 프로그래머가 알아야할 지식의 양은 줄고, 클래스 작 성자가 자유롭게 구현을 변경할 수 있는 폭이 넓어진다.
- 따라서 클래스를 개발할 때마다 인터페이스와 구현을 깔끔하게 분리하기 위해 노력해야 한다.

### 메시지와 메서드
- 객체는 다른 객체에게 퍼블릭 인터페이스 행동하도록 요청(메시지를 보내는 것)할 수 있다.
- 수신된 메시지를 처리하기 위한 자신만의 방법을 메서드(method)라고 부른다.
- 메시지와 메서드의 구분으로 `다형성`의 개념이 출발한다.

## 상속과 다형성
```java
public class Movie {
    private String title;
    private Duration runningTime;
    private Money fee;
    private DiscountPolicy discountPolicy; // 할인정책

    public Movie(String title, Duration runningTime, Money fee, DiscountPolicy discountPolicy) {
        this.title = title;
        this.runningTime = runningTime;
        this.fee = fee;
        this.discountPolicy = discountPolicy;
    }

    public Money getFee() {
        return fee;
    }

    public Money calculateMovieFee(Screening screening) {
        return fee.minus(discountPolicy.calculateDiscountAmount(screening));
    }
}
```
- calculateMovieFee메서드에는 상속과 다형성의 개념이 적용되어 있고, 그 기반에는 추상화라는 원리가 숨겨져 있다.

```java
public abstract class DiscountPolicy {
    private List<DiscountCondition> conditions = new ArrayList<>();

    public DiscountPolicy(DiscountCondition ... conditions) {
        this.conditions = Arrays.asList(conditions);
    }

    public Money calculateDiscountAmount(Screening screening) {
        for(DiscountCondition each : conditions) {
            if (each.isSatisfiedBy(screening)) {
                return getDiscountAmount(screening);
            }
        }

        return Money.ZERO;
    }

    abstract protected Money getDiscountAmount(Screening Screening);
}
```
- 부모 클래스를 만들어 중복 코드를 두고 상속 받게 할것이다.
    - DiscountPolicy는 요금 계산에 필요한 전체적인 흐름은 정의하지만 실제로 요금을 계산하는 부분은 추상 메서드인 getDiscountAmount 메서드에게 위임한다.
- 이처럼 부모 클래스에 기본적인 알고리즘의 흐름을 구현하고 중간에 필요한 처리를 자식 클래스에게 위임하는 디자인 패턴을 TEMPLATE METHOD 패턴이라고 부른다.
```java
public interface DiscountCondition {

    boolean isSatisfiedBy(Screening screening);
}
```
DiscountCondition은 인터페이스로 선언한다.
isSatisfiedBy은 인자로 전달된 screening이 할인 되는 경우 true를 반환한다. 


### 컴파일 시간 의존성과 실행 시간 의존성
앞선 예제에서 Movie 클래스는 추상클래스인 DiscountPolicy를 의존한다.
코드 작성 시점에는 그 존재조차 알지 못한 자식 클래스의 인스턴스와 협력 가능한 이유는? 

```java
Movie avatar = new Movie("아바타", Duration.ofMinutes(120), Money.wons(10000),
    new AmountDiscountPolicy(Money.wons(800), 
            new SequenceCondition(1),
            new PeriodCondition(DayOfWeek.MONDAY, LocalTime.of(10, 0), LocalTime.of(11,59)))
);
```
실행 시점에는 Movie 클래스의 인스턴스를 생성할 때 구체적인 할인 정책을 결정한다.
코드의 의존성과 실행 시점의 의존성이 서로 다를 수 있다. (다시 말해 클래스 사이의 의존성과 객체 사이의 의존성은 동일하지 않을 수 있다 ??)

#### 트레이드 오프 
코드를 이해하기 위해서는 코드뿐만 아니라 객체를 생성하고 연결하는 부분을 찾아야 하기 때문에,
코드의 의존성과 실행 시점의 의존성이 다르면 다를수록 코드를 이해하기 어려워진다.
반면 코드의 의존성과 실행 시점의 의존성이 다르면 다를수록 코드는 더 유연해지고 확장 가능해진다. 
이와 같은 의존성의 양면성은 설계가 트레이드오프의 산물이라는 사실을 잘 보여준다.

==> 항상 유연성과 가독성 사이에 서 고민해야 한다.

### 상속과 인터페이스 
대부분의 사람들은 상속의 목적이 메서드나 인스턴스 변수를 재사용하는 것이라고 생각한다.  
상속이 가치 있는 이유는 부모 클래스가 제공하는 모든 인터페이스를 자식 클래스가 물려받기 때문이다.

상속을 통해 자식 클 래스는 자신의 인터페이스에 부모 클래스의 인터페이스를 포함하게 된다.   
결과적으로 **자식 클래스는 부모 클래스가 수신할 수 있는 모든 메시지를 수신할 수 있기 때문**에,  
외부 객체는 자식 클래스를 부모 클래스와 동일한 타입으로 간주할 수 있다.

**컴파일러는 코드상에서 부모 클래스가 나오는 모든 장소에서 자식 클래스를 사용하는 것을 허용**한다.  
이처럼 자식 클래스가 부모 클래스를 대신하는 것을 업캐스팅(upcasting)이라고 부른다.

### 다형성
다시 한번 강조하지만 메시지와 메서드는 다른 개념이다.  

Movie는 동일한 메시지를 전송하지만 실제로 어떤 메서드가 실행될 것인지는 메시지를 수신하는 객체의 클래스가 무엇이냐에 따라 달라진다.   
이를 다형성이라고 부른다.  

다형성은 객체지향 프로그램의 컴파일 시간 의존성과 실행 시간 의존성이 다를 수 있다는 사실을 기반으로 한다.  

예제에서 컴파일 시간 의존성은 Movie에서 DiscountPolicy로 향한다.
실행 시간 의존성은 Movie에서 자식 클래스(AmountDiscountPolicy나 PercentDiscountPolicy)로 향한다.   
이처럼 다형성은 컴파일 시간 의존성과 실행 시간 의존성을 다르게 만들 수 있는 객체지향의 특성을 이용해 서로 다른 메서드를 실행할 수 있게 한다.

**다형성이란 동일한 메시지를 수신했을 때 객체의 타입에 따라 다르게 응답할 수 있는 능력을 의미한다.  
따라서 다형적인 협력에 참여하는 객체들은 모두 같은 메시지를 이해할 수 있어야 한다. 다시 말해 인터페이스가 동일해야 한다**

다형성을 구현하는 방법은 다양하지만, 메시지에 응답하기 위해 실행될 메서드를 컴파일 시점이 아닌 실행 시점에 결정한다는 공통점이 있다.  
다시 말해, 메시지와 메서드를 실행시점에 바인딩한다는 것이다.  
이를 지연 바인딩(lazy binding) 또는 동적 바인딩(dynamic binding)이라고 부른다.  

객체지향이 컴파일 시점의 의존성과 실행 시점의 의존성을 분리하고, 하나의 메시지를 선택적으로 서로 다른 메서드에 연결할 수 있는 이유가 바로   
지연 바인딩이라는 메커니즘을 사용하기 때문이다.


#### 구현 상속과 인터페이스 상속
흔히 구현 상속을 서브클래싱(subclassing)이라고 부르고 인터페이스 상속을 서브타이핑(subtyping)이라고 부른다.
순수하게 **코드를 재사용하기 위한 목적으로 상속을 사용하는 것을 구현 상속**이라고 부른다.  
다형적인 협력을 위해 부모 클래스와 자식 클래스가 **인터페이스를 공유할 수 있도록 상속을 이용하는 것을 인터페이스 상속**이라고 부른다.

`상속은 구현 상속이 아니라 인터페이스 상속을 위해 사용해야 한다`. 대부분의 사람들은 코드 재사용을 상속의 주된 목적이라고 생각하지만 이것은 오해다.  
인터페이스를 재사용할 목적이 아니라 구현을 재사용할 목적으로 상속을 사용하면 변경에 취약한 코드를 낳게 될 확률이 높다.

### 인터페이스와 다형성
DiscountPolicy를 **추상클래스로 구현하므로써 자식 클래스들이 인터페이스와 내부 구현을 함께 상속**받도록 만들었다. 
**이와 달리 구현은 공유할 필요가 없고 순수하게 인터페이스만 공유하고 싶을 때가 사용되는 것이 바로 Interface이다.** 

자바의 인터페이스는 말 그대로 구현에 대한 고려 없이 다형적인 협력에 참여하는 클래스들이 공유 가능한 외부 인터페이스를 정의한 것이다.

할인 정책과 달리 할인 조건은 구현을 공유할 필요가 없기 때 문에 자바의 인터페이스를 이용해 타입 계층을 구현했다.

## 추상화와 유연성 
추상화를 사용하면 세부적인 내용을 무시한 채 상위 정책을 쉽고 간단하게 표현할 수 있다.

금액 할인 정책과 비율 할인 정책을 사용한다는 사실이 중요할 때도 있겠지만   
어떤 때는 할인 정책 이 존재한다고 말하는 것만으로도 충분한 경우가 있다.  
추상화를 이용한 설계는 필요에 따라 표현의 수준을 조정하는 것을 가능하게 해준다.

추상화를 이용해 상위 정책을 기술한다는 것은 기본적인 애플리케이션의 협력 흐름을 기술한다는 것 을 의미한다.

`자식 클래스들은 추상화를 이용해서 정의한 상위의 협력 흐름을 그대로 따르게 된다.`   
이 개념은 매우 중요한데, 재사용 가능한 설계의 기본을 이루는 디자인 패턴(design pattern)이나 프레임워크(framework) 모두   
추상화를 이용해 상위 정책을 정의하는 객체지향의 메커니즘을 활용하고 있기 때문이다.

```java
import org.eternity.movie.Screening;

public class Movie {

  public Money calculateMovieFee(Screening screening){
      if(discountPolicy == null){
          return fee;
      }
      
      return fee.minus(discountPolicy.calculateDiscountAmount(screening));
  }
}
```
할인정책이 적용돼 있지 않은 경우, 계산할 필요없이 기본 금액을 그대로 돌려주면 된다.

일관성 있던 협력 방식이 무너졌다. 
기존 할인 정책의 경우 할인할 금액을 계산하는 책임이 DiscountPolicy의 자식 클래스에 있었기 때문이다. 

따라서 책임의 위치를 결정하기 위해 조건문을 사용하는 것은 협력의 설계 측면에서 대부분의 경우 좋지 않은 선택이다.
책임의 일관성을 유지해보자.
```java
public class NoneDiscountPolicy extends DiscountPolicy {
    @Override
    protected Money getDiscountAmount(Screening screening) {
        return Money.ZERO;
    }
}
```
```java
Movie avatar = new Movie("스타워즈", Duration.ofMinutes(120), Money.wons(10000),
    new NoneDiscountPolicy());
```
중요한 것은 기존의 Movie와 DiscountPolicy를 수정하지 않고 새로운 클래스를 추가하는 것만으로
애플리케이션의 기능을 확장했다는 것이다. 

추상화가 유연한 설계를 가능하게 하는 이유는 설계가 구체적인 상황에 결합되는 것을 방지하기 때문이다.  

Movie가 특정한 할인 정책에 묶이지 않는다. DiscountPolicy를 상속받는 어떤 클래스와도 협력이 가능하다.  
DiscountPolicy역시 특정한 할인 조건에 묶여있지 않다. DiscountCondition을 상속받는 어떤 클래스와도 협력이 가능하다. 

==> 유연성이 필요한 곳에 추상화를 사용하라. 

### 추상클래스와 인터페이스 트레이드오프
NoneDiscountPolicy을 다시 살펴보자.  

부모 클래스인 DiscountPolicy에서 할인 조건이 없을 경우에는 getDiscountAmount() 메서드를 호출하지 않기 때문에 
getDiscountAmount() 메서드가 어떤 값을 반환하던 상관이 없는 상황이다.

```java
public interface DiscountPolicy {
    Money calculateDiscountAmount(Screening screening);
}
```
```java
public abstract class DefaultDiscountPolicy implements DiscountPolicy {
    private List<DiscountCondition> conditions = new ArrayList<>();

    public DefaultDiscountPolicy(DiscountCondition... conditions) {
        this.conditions = Arrays.asList(conditions);
    }

    @Override
    public Money calculateDiscountAmount(Screening screening) {
        for(DiscountCondition each : conditions) {
            if (each.isSatisfiedBy(screening)) {
                return getDiscountAmount(screening);
            }
        }

        return Money.ZERO;
    }

    abstract protected Money getDiscountAmount(Screening Screening);
}
```
이제 NoneDiscountPolicy는 DiscountPolicy 인터페이스를 직접 구현할 수 있다.

```java
public class NoneDiscountPolicy implements DiscountPolicy {
    @Override
    public Money calculateDiscountAmount(Screening screening) {
        return Money.ZERO;
    }
}
```

이상적으로는 인터페이스를 사용하도록 변경한 설계가 더 좋을 수 있지만,
NoneDiscountPolicy만을 위해 인터페이스를 추가하는 것이 과하다는 생각이 들 수도 있다. 

구현과 관련된 모든 것들이 트레이드오프의 대상이 될 수 있다.  
작성하는 모든 코드에는 합당한 이유가 있어야 한다.  
비록 아주 사소한 결정이더라 도 트레이드 오프를 통해 얻어진 결론과 그렇지 않은 결론 사이의 차이는 크다.  
고민하고 트레이드 오프하라.

### 상속보다 합성 
상속보다는 합성(composition)이 더 좋은 방법이라는 이야기를 많이 들었을 것이다.   
합성은 다른 객체의 인스턴스를 자신의 인스턴스 변수로 포함해서 재사용하는 방법을 말한다.  
(Movie가 DiscountPolicy를 포함하는 것처럼)

#### 상속 
상속의 가장 큰 문제점은 캡슐화를 위반한다는 것이다.  
상속을 이용하기 위해서는 부모 클래스의 내부 구조를 잘 알고 있어야 한다.

캡슐화의 약화는 자식 클래스가 부모 클래스에 강하게 결합되도록 만들기 때문에 부모 클래스를 변경할 때 자식 클래 스도 함께 변경될 확률을 높인다.    
결과적으로 상속을 과도하게 사용한 코드는 변경하기도 어려워진다.  

상속의 두 번째 단점은 설계가 유연하지 않다는 것이다.   
상속은 부모 클래스와 자식 클래스 사이의 관계를 컴파일 시점에 결정한다.  
따라서 실행 시점에 객체의 종류를 변경하는 것이 불가능하다.

반면 인스턴스 변수로 연결한 기존 방법을 사용하면 실행 시점에 할인 정책을 간단하게 변경할 수 있 다. 
다음과 같이 Movie에 DiscountPolicy를 변경할 수 있는 changeDiscountPolicy 메서드를 추가하자.

```java
public class Movie {

    public void changeDiscountPolicy(DiscountPolicy discountPolicy) {
        this.discountPolicy = discountPolicy;
    }
}
```
| 구분 | 상속 (Inheritance) | 컴포지션 (Composition) |
|---|---|---|
| 관계 | is-a (자식은 부모의 한 종류다) | as-a (객체가 다른 객체를 가진다) |
| 결합 시점 | 컴파일 시점 *(정적)* | 실행 시점 *(동적)* |
| 유연성 | 낮음 *(한번 정해진 타입은 변경 불가)* | 높음 *(내부 부품을 언제든 교체 가능)* |
| 비유 | 자식의 부모를 바꿀 수 없는 것 | 리모컨의 건전지를 바꾸는 것 |

#### 합성
Movie는 요금을 계산하기 위해 DiscountPolicy의 코드를 재사용한다.

이 방법이 상속과 다른 점은 상속 이 부모 클래스의 코드와 자식 클래스의 코드를 컴파일 시점에 하나의 단위로 강하게 결합하는 데 비해  
Movie가 DiscountPolicy의 인터페이스를 통해 약하게 결합된다는 것이다.  

실제로 Movie는 DiscountPolicy 가 외부에 calculateDiscountAmount 메서드를 제공한다는 사실만 알고 내부 구현에 대해서는 전혀 알 지 못한다.  
<mark>이처럼 인터페이스에 정의된 메시지를 통해서만 코드를 재사용하는 방법을 합성이라고 부른다.</mark>


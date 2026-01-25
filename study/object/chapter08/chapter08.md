# CHAPTER 08 의존성 관리하기
잘 설계된 객체지향 애플리케이션은 작고 응집도 높은 객체들로 구성된다. 작고 응집도 높은 객체란 책임의 초점이 명확하고 한 가지 일만 잘 하는 객체를 의미한다.  
이런 작은 객체들이 단독으로 수행할 수 있는 작업은 거의 없기 때문에 일반적인 애플리케이션의 기능을 구현하기 위해서는 다른 객체에게 도움을 요청해야 한다. 이런 요청이 객체 사이의 협력을 낳는다.  

협력은 필수적이지만 과도한 협력은 설계를 곤경에 빠트릴 수 있다. 협력은 객체가 다른 객체에 대해 알 것을 강요한다.  
다른 객체와 협력하기 위해서는 그런 객체가 존재한다는 사실을 알고 있어야 한다. 객체가 수신할 수 있는 메시지에 대해서도 알고 있어야 한다.  
이런 지식이 객체 사이의 의존성을 낳는다.  

객체지향 설계의 핵심은 협력을 위해 필요한 의존성은 유지하면서도 변경을 방해하는 의존성은 제거하는 데 있다.  
이런 관점에서 객체지향 설계란 의존성을 관리하는 것이고 객체가 변화를 받아들일 수 있게 의존성을 정리하는 기술이라고 할 수 있다.  

## 01 의존성 이해하기
### 변경과 의존성
어떤 객체가 협력하기 위해 다른 객체를 필요로 할 때 두 객체 사이에 의존성이 존재하게 된다. 의존성 은 실행 시점과 구현 시점에 서로 다른 의미를 가진다.
- 실행 시점 의존하는 객체가 정상적으로 동작하기 위해서는 실행 시에 의존 대상 객체가 반드시 존재해야 한다. 
- 구현 시점 의존 대상 객체가 변경될 경우 의존하는 객체도 함께 변경된다.

```java
public class PeriodCondition implements DiscountCondition {
    private DayOfWeek dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;

    public PeriodCondition(DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime) {
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    @Override
    public boolean isSatisfiedBy(Screening screening) {
        return dayOfWeek.equals(screening.getWhenScreened().getDayOfWeek()) &&
            startTime.compareTo(screening.getWhenScreened().toLocalTime()) <= 0&&
            endTime.compareTo(screening.getWhenScreened().toLocalTime()) >= 0;
    }
}
```
![](https://readwise-assets.s3.amazonaws.com/media/reader/pub/332515f7b8a1ee068810bb69701fac0d.png)
어떤 형태로든 DayOfWeek, LocalTime, Screening, DiscountCondition이 변경된다면 PeriodCondition도 함께 변경될 수 있다.  
![](https://readwise-assets.s3.amazonaws.com/media/reader/pub/938cc2001eca5638bc52547a0348f164.png)

### 의존성 전이
의존성은 전이될 수 있다.
의존성 전이(transitive dependency)가 의미하는 것은 PeriodCondition이 Screening에 의존할 경우 PeriodCondition은 Screening이 의존하는 대상에 대해서도 자동적으로 의존하게 된다는 것이다.  
다시 말해서 Screening이 가지고 있는 의존성이 Screening에 의존하고 있는 PeriodCondition으로도 전파된다는 것이다.  
따라서 Screening이 Movie, LocalDateTime, Customer에 의존 하기 때문에 PeriodCondition 역시 간접적으로 Movie, LocalDateTime, Customer에 의존하게 된다.
![](https://readwise-assets.s3.amazonaws.com/media/reader/pub/91320d1bdf3222cadd8ab1c409674061.png)

의존성 전이는 변경에 의해 영향이 널리 전파될 수도 있다는 경고일 뿐이다.  
의존성은 전이될 수 있기 때문에 의존성의 종류를 직접 의존성(direct dependency)과 간접 의존성 (indirect dependency)으로 나누기도 한다.  
직접 의존성이란 말 그대로 한 요소가 다른 요소에 직접 의존하는 경우를 가리킨다. Periodcondition이 Screening에 의존하는 경우가 여기에 속하며, 이 경우 의존성은 PeriodCondition의 코드에 명시적으로 드러난다.  
간접 의존성이란 직접적인 관계는 존재하지 않지만 의존성 전이에 의해 영향이 전파되는 경우를 가리킨다. 이 경우 의존성은 PeriodCondition의 코드 안에 명시적으로 드러나지 않는다.

### 런타임 의존성과 컴파일타임 의존성
런타임은 간단하다. 말 그대로 애플리케이션이 실행되는 시점을 가리킨다.  
일반적으로 컴파일타임이란 작성된 코드를 컴파일하는 시점을 가리키지만 문맥에 따라서는 코드 그 자체를 가리키기도 한다.  
어딘가에서 컴파일타임이라는 용어를 보게 된다면 그것이 정말 컴파일이 진행되는 시점을 가리키는 것인지 아니면 코드를 작성하는 시점을 가리키는 것인지를 파악하는 것이 중요하다.  

런타임 의존성이 다루는 주제는 객체 사이의 의존성이다.  
반면 코드 관점에서 주인공은 클래스다. 따라서 컴파일타임 의존성이 다루는 주제는 클래스 사이의 의존성이다.

Movie는 AmountDiscountPolicy와 PercentDiscountPolicy 모두와 협력할 수 있어야 한다.  
AmountDiscountPolicy와 PercentDiscountPolicy가 추상 클래스인 DiscountPolicy를 상속받게 한 후 Movie가 이 추상 클래스에 의존하도록 클래스 관계를 설계했다.  
![](https://readwise-assets.s3.amazonaws.com/media/reader/pub/7755b30a4e526b9d5eff83d996cd4bac.png)

Movie 클래스는 오직 추상 클래스인 DiscountPolicy 클래스에만 의존한다.  
코드를 작성하는 시점의 Movie 클래스는 AmountDiscountPolicy 클래스와 PercentDiscountPolicy 클래스의 존재에 대해 전혀 알지 못하지만 실행 시점의 Movie 인스턴스는 AmountDiscountPolicy 인스턴스와 PercentDiscountPolicy 인스턴스와 협력할 수 있어야 한다.  
![](https://readwise-assets.s3.amazonaws.com/media/reader/pub/d78aa849b3d82d4aa81829dd2463d428.png)

Movie의 인스턴스가 이 두 클래스의 인스턴스와 함께 협력할 수 있게 만드는 더 나은 방법은 두 클래스 모두를 포괄하는 DiscountPolicy라는 추상 클래스에 의존하도록 만들고  
이 컴파일타임 의존성을 실행 시에 PercentDiscountPolicy 인스턴스 나 AmountDiscountPolicy 인스턴스에 대한 런타임 의존성으로 대체해야 한다.  

어떤 클래스의 인스턴스가 다양한 클래스의 인스턴스와 협력하기 위해서는 협력할 인스턴스의 구체적인 클래스를 알아서는 안 된다. 실제로 협력할 객체가 어떤 것인지는 런타임에 해결해야 한다.  

### 컨텍스트 독립성
클래스는 자신과 협력할 객체의 구체적인 클래스에 대해 알아서는 안된다.  
구체적인 클래스를 알면 알수록 그 클래스가 사용되는 특정한 문맥에 강하게 결합되기 때문이다.  
Movie 클래스에 추상 클래스인 DiscountPolicy에 대한 컴파일 타임 의존성을 명시하는 것은 Movie가 할인 정책에 따라 요금을 계산하지만 구체적으로 어떤 정책을 따 르는지는 결정하지 않았다고 선언하는 것이다.  
클래스가 사용 될 특정한 문맥에 대해 최소한의 가정만으로 이뤄져 있다면 다른 문맥에서 재사용하기가 더 수월해진다.  
이를 컨텍스트 독립성이라고 부른다.

### 의존성 해결하기
컴파일타임 의존성을 실행 컨텍스트에 맞는 적절한 런타임 의존성으로 교체하는 것을 의존성 해결이라고 부른다. 의존성을 해결하기 위해서는 일반적으로 다음과 같은 세 가지 방법을 사용한다.  
- 객체를 생성하는 시점에 생성자를 통해 의존성 해결 
- 객체 생성 후 setter 메서드를 통해 의존성 해결 
- 메서드 실행 시 인자를 이용해 의존성 해결

## 02 유연한 설계
### 의존성과 결합도
객체지향 패러다임의 근간은 협력이다. 객체들은 협력을 통해 애플리케이션에 생명력을 불어넣는다.  
객체들이 협력하기 위해서는 서로의 존재와 수행 가능한 책임을 알아야 한다. 이런 지식들이 객체 사이의 의존성을 낳는다. 따라서 모든 의존성이 나쁜 것은 아니다.  
의존성은 객체들의 협력을 가능하게 만드는 매개체라는 관점에서는 바람직한 것이다. 하지만 의존성이 과하면 문제가 될 수 있다.  

Movie와 PercentDiscountPolicy 사이에 의존성이 존재하는 것은 문제가 아니다. 오히려 이 의존성이 객체 사이의 협력을 가능하게 만들기 때문에 존재 자체는 바람직한 것이다.  
문제는 의존성의 존재가 아니라 의존성의 정도다. 이 코드는 Movie를 PercentDiscountPolicy라는 구체 적인 클래스에 의존하게 만들기 때문에 다른 종류의 할인 정책이 필요한 문맥에서 Movie를 재사용할 수 있는 가능성을 없애 버렸다.  

<mark>그렇다면 바람직한 의존성이란 무엇인가?</mark> 바람직한 의존성은 재사용성과 관련이 있다.  
어떤 의존성이 다양한 환경에서 클래스를 재사용할 수 없도록 제한한다면 그 의존성은 바람직하지 못한 것이다.  
어떤 의존성이 다양한 환경에서 재사용할 수 있다면 그 의존성은 바람직한 것이다.  
다시 말해 컨텍스트에 독립적인 의존성은 바람직한 의존성이고 특정한 컨텍스트에 강하게 결합된 의존성은 바람직하지 않은 의존성이다.  
다른 환경에서 재사용하기 위해 내부 구현을 변경하게 만드는 모든 의존성은 바람직하지 않은 의존성이다.  

바람직한 의존성이란 컨텍스트에 독립적인 의존성을 의미하며 다양한 환경에서 재사용될 수 있 는 가능성을 열어놓는 의존성을 의미한다.  
어떤 두 요소 사이에 존재하는 의존성이 바람직할 때 두 요소가 느슨한 결합도(loose coupling) 또는 약한 결합도 (weak coupling)를 가진다고 말한다. 반 대로 두 요소 사이의 의존성이 바람직하지 못할 때 단단한 결합도(tight coupling) 또는 강한 결합도 (strong coupling)를 가진다고 말한다.
#### 의존성과 결합도
일반적으로 의존성과 결합도를 동의어로 사용하지만 사실 두 용어는 서로 다른 관점에서 관계의 특성을 설명하는 용어다.  
<mark>의존성은 두 요소 사이의 관계 유무를 설명한다.</mark> 따라서 의존성의 관점에서는 "의존성이 존재한다" 또는 "의존성이 존재하지 않는다"라고 표현해야 한다.  
그에 반해 <mark>결합도는 두 요소 사이에 존재하는 의존성의 정도를 상대적</mark>으로 표현한다. 따라서 결합도의 관점에서는 "결합도가 강하다" 또는 "결합도가 느슨하다"라고 표현한다.  
어떤 의존성이 재사용을 방해한다면 결합도가 강하다고 표현한 다. 어떤 의존성이 재사용을 쉽게 허용한다면 결합도가 느슨하다고 표현한다.

### 지식이 결합을 낳는다
Movie 클래스가 추상 클래스인 DiscountPolicy 클래스에 의존하는 경우에는 구체적인 계산 방법은 알 필요가 없다. 그저 할인 요금을 계산한다는 사실만 알고 있을 뿐이다.    
따라서 Movie가 PercentDiscountPolicy에 의존하는 것보다 DiscountPolicy에 의존하는 경우 알아야 하는 지식의 양이 적 기 때문에 결합도가 느슨해지는 것이다.

### 추상화에 의존하라
추상화란 어떤 양상, 세부사항, 구조를 좀 더 명확하게 이해하기 위해 특정 절차나 물체를 의도적으로 생략하거나 감춤으로써 복잡도를 극복하는 방법이다  
추상화를 사용하면 현재 다루고 있는 문제를 해결하는 데 불필요한 정보를 감출 수 있다. 따라서 대상에 대해 알아야 하는 지식의 양을 줄 일 수 있기 때문에 결합도를 느슨하게 유지할 수 있다.  
Movie와 DiscountPolicy 사이의 결합도가 더 느슨한 이유는 Movie가 구체적인 대상이 아닌 추상화에 의존하기 때문이다.  
일반적으로 추상화와 결합도의 관점에서 의존 대상을 다음과 같이 구분하는 것이 유용하다. 
- 구체 클래스 의존성(concrete class dependency)
- 추상 클래스 의존성(abstract class dependency)
- 인터페이스 의존성(interface dependency)

구체 클래스에 비해 추상 클래스는 메서드의 내부 구현과 자식 클래스의 종류에 대한 지식을 클라이언트에게 숨길 수 있다.  
따라서 클라이언트가 알아야 하는 지식의 양이 더 적기 때문에 구체 클래스보다 추상 클래스에 의존하는 것이 결합도가 더 낮다.  
하지만 추상 클래스의 클라이언트는 여전히 협력하는 대상이 속한 클래스 상속 계층이 무엇인지에 대해서는 알고 있어야 한다.  

인터페이스 의존성은 협력하는 객체가 어떤 메시지를 수신할 수 있는지에 대한 지식만을 남기기 때문에 추상 클래스 의존성보다 결합도가 낮다.  
이것은 다양한 클래스 상속 계층에 속한 객체들이 동일한 메시지를 수신할 수 있도록 컨텍스트를 확장하는 것을 가능하게 한다.  
<mark>의존하는 대상이 더 추상적일수록 결합도는 더 낮아진다</mark>는 것이다. 이것이 핵심이다.  

### 명시적인 의존성
인스턴스 변수의 타입을 추상 클래 스나 인터페이스로 선언하는 것만으로는 부족하다. 클래스 안에서 구체 클래스에 대한 모든 의존성을 제거해야만 한다.  
하지만 런타임에 Movie는 구체 클래스의 인스턴스와 협력해야 하기 때문에 Movie의 인스턴스가 AmountDiscountPolicy의 인스턴스인지 PercentDiscountPolicy의 인스턴스인지를 알려줄 수 있는 방법이 필요하다.  
다시 말해서 Movie의 의존성을 해결해 줄 수 있는 방법이 필요한 것이다.  

의존성의 대상을 생성자의 인자로 전달받는 방법과 생성자 안에서 직접 생성하는 방법 사이의 가장 큰 차이점은 퍼블릭 인터페이스를 통해 할인 정책을 설정할 수 있는 방법을 제공하는지 여부다.  
유연하고 재사용 가능한 설 계란 퍼블릭 인터페이스를 통해 의존성이 명시적으로 드러나는 설계다.   
명시적인 의존성을 사용해야만 퍼블릭 인터페이스를 통해 컴파일타임 의존성을 적절한 런타임 의존성으로 교체할 수 있다.

### new는 해롭다
new를 잘못 사용하면 클래스 사이의 결합도가 극단적으로 높아진다. 결합도 측면에서 new가 해로운 이유는 크게 두 가지다.
- new 연산자를 사용하기 위해서는 구체 클래스의 이름을 직접 기술해야 한다. 따라서 new를 사용하는 클라이언트는 추상화 가 아닌 구체 클래스에 의존할 수밖에 없기 때문에 결합도가 높아진다. 
- new 연산자는 생성하려는 구체 클래스뿐만 아니라 어떤 인자를 이용해 클래스의 생성자를 호출해야 하는지도 알아야 한다. 따라서 new를 사용하면 클라이언트가 알아야 하는 지식의 양이 늘어나기 때문에 결합도가 높아진다.

결합도의 관점에서 구체 클래스는 협력자에게 너무 많은 지식을 알도록 강요한다. 여기에 new는 문제를 더 크게 만든다.  
클라이언트는 구체 클래스를 생성하는 데 어떤 정보가 필요한지에 대해서도 알아야 하기 때문이다.  
![](https://readwise-assets.s3.amazonaws.com/media/reader/pub/4924713a06190a7eaac68e042fcf73be.png)

Movie가 DiscountPolicy에 의존해야 하는 유일한 이유는 calculateDiscountAmount 메시지를 전송하기 위해서다.  
따라서 메시지에 대한 의존성 외의 모든 다른 의존성은 Movie의 결 합도를 높이는 불필요한 의존성이다. new는 이런 불필요한 겹합도를 급격하게 높인다.  

해결 방법은 인스턴스를 생성하는 로직과 생성된 인스턴스를 사용하는 로직을 분리하는 것이다.
이를 위해 Movie는 외부로부터 이미 생성된 AmountDiscountPolicy의 인스턴스를 전달받아야 한다.
어떤 방법을 사용하건 Movie 클레스에는 AmountDiscountPolicy의 인스턴스에 메시지를 전송하는 코드만 남아 있어야 한다.

Movie는 단지 메시지를 전송하는 단 하나의 일만 수행 한다. 그렇다면 누가 AmountDiscountPolicy의 인스턴스를 생성하는가? Movie의 클라이언트가 처리한다.   
이제 AmountDiscountPolicy의 인스턴스를 생성하는 책임은 Movie의 클라이언트로 옮겨지고 Movie는 AmountDiscountPolicy의 인스턴스를 사용하는 책임만 남는다.

사용과 생성의 책임을 분리하고, 의존성을 생성자에 명시적으로 드러내고, 구체 클래스가 아닌 추상 클래스에 의존하게 함으로써 설계를 유연하게 만들 수 있다.  
그리고 그 출발은 객체를 생성하는 책임을 객체 내부가 아니라 클라이언트로 옮기는 것에서 시작했다는 점을 기억하라.

### 가끔은 생성해도 무방하다
클래스 안에서 객체의 인스턴스를 직접 생성하는 방식이 유용한 경우도 있다. 주로 협력하는 기본 객체를 설정하고 싶은 경우가 여기에 속한다.  
이 문제를 해결하는 방법은 기본 객체를 생성하는 생성자를 추가하고 이 생성자에서 DiscountPolicy의 인스턴스를 인자로 받는 생성자를 체이닝하는 것이다  

```java
public class Movie{
    private DiscountPolicy discountPolicy;
    
    public Movie(String title, Duration runningTime, Money fee){
        this(title, runningTime, fee, new AmountDiscountPolicy(...));
    }
    
    public Movie(String title, Duration runningTime, Money fee, DiscountPolicy discountPolicy){
        ...
        this.discountPolicy = discountPolicy;
    }
}
```
여 기서 눈여겨볼 부분은 첫 번째 생성자의 내부에서 두 번째 생성자를 호출한다는 것이다. 다시 말 해 생성자가 체인처럼 연결된다.  
이제 클라이언트는 대부분의 경우에 추가된 간략한 생성자를 통해 AmountDiscountPolicy의 인스턴스와 협력하게 하면서도 컨텍스트에 적절한 DiscountPolicy의 인스턴스 로 의존성을 교체할 수 있다.  
이 방법은 메서드를 오버로딩하는 경우에도 사용할 수 있다.  
다음과 같이 DiscountPolicy의 인스턴스를 인자로 받는 메서드와 기본값을 생성하는 메서드를 함께 사용한다면 클래스의 사용성을 향상시키면서 도 다양한 컨텍스트에서 유연하게 사용될 수 있는 여지를 제공할 수 있다.  
```java
public class Movie{
    private DiscountPolicy discountPolicy;
    
    public Money calculateMovieFee(Screening screening){
        return calculateMovieFee(screening, new AmountDiscountPolicy(...));
    }
    
    public Money calculateMovieFee(Screening screening, DiscountPolicy discountPolicy){
        return fee.minus(discountPolicy.calculateDiscountAmount(screening));
    }
}
```
이 예는 설계가 트레이드오프 활동이라는 사실을 다시 한번 상기시킨다. 여기서 트레이드 오프의 대상은 결합도와 사용성이다.  

### 표준 클래스에 대한 의존은 해롭지 않다
의존성이 불편한 이유는 그것이 항상 변경에 대한 영향을 암시하기 때문이다. 따라서 변경될 확률이 거 의 없는 클래스라면 의존성이 문제가 되지 않는다.   
JDK의 표준 컬렉션 라이브러리에 속하는 ArrayList의 경우에는 다음과 같이 직접 생성해서 대입하는 것이 일반적이다.  
ArrayList의 코드가 수정될 확률은 0에 가깝기 때문에 인스턴스를 직접 생 성하더라도 문제가 되지 않기 때문이다.  

### 컨텍스트 확장하기
두 가지 예를 살펴보겠다. 하나는 할인 혜택을 제공하지 않는 영화의 경우이고, 다른 하나는 다수의 할 인 정책을 중복해서 적용하는 영화를 처리하는 경우다.  
첫 번째는 할인 혜택을 제공하지 않는 영화의 예매 요금을 계산하는 경우다.

```java
public class Movie{
    public Movie(String title, Duration runningTime, Money fee) {
        this(title, runningTime, fee, null);
    }
    
    public Movie(String title, Duration runningTime, Money fee, DiscountPolicy discountPolicy) {
        ...
        this.discountPolicy = discountPolicy;
    }
    
    public Money calculateMovieFee(Screening screening) {
        if (discountPolicy == null) {
            return fee;
        }
        return fee.minus(discountPolicy.calculateDiscountAmount(screening));
    }
}
```
이 코드는 제대로 동작하지만 한 가지 문제가 있다. 지금까지의 Movie와 DiscountPolicy 사이의 협력 방 식에 어긋나는 예외 케이스가 추가된 것이다.  
해결책은 할인 정책이 존재하지 않는다는 사실을 예외 케이스로 처리하지 말고 기존에 Movie와 DiscountPolicy가 협력하던 방식을 따르도록 만드는 것이다.  
다시 말해 할인 정책이 존재하지 않는다는 사실을 할인 정책의 한 종류로 간주하는 것이다.

```java
public class NoneDiscountPolicy extends DiscountPolicy {
    @Override
    protected Money calculateDiscountAmount(Screening screening) {
        return Money.ZERO;
    }
}
```

두 번째 예는 중복 적용이 가능한 할인 정책을 구현하는 것이다.
할인 정책을 중복해서 적용하기 위해서는 Movie가 하나 이상의 DiscountPolicy와 협력할 수 있어야 한다.  
가장 간단하게 구현할 수 있는 방법은 Movie가 DiscountPolicy의 인스턴스들로 구성된 List를 인스턴스 변수로 갖게 하는 것이다.  
하지만 이 방법은 중복 할인 정책을 구현하기 위해 기존의 할인 정책의 협력 방식과는 다른 예외 케이스를 추가하게 만든다.  
이 문제 역시 NoneDiscountPolicy와 같은 방법을 사용해서 해결할 수 있다. 중복 할인 정책을 할인 정책 의 한 가지로 간주하는 것이다.  
중복 할인 정책을 구현하는 OverlappedDiscountPolicy를 DiscountPolicy 의 자식 클래스로 만들면 기존의 Movie와 DiscountPolicy 사이의 협력 방식을 수정하지 않고도 여러 개 의 할인 정책을 적용할 수 있다.

```java
public class OverlappedDiscountPolicy extends DiscountPolicy {
    private List<DiscountPolicy> discountPolicies;
    
    public OverlappedDiscountPolicy(DiscountPolicy... discountPolicies) {
        this.discountPolicies = discountPolicies;
    }
    
    @Override
    protected Money calculateDiscountAmount(Screening screening) {
        Money result = Money.ZERO;
        for (DiscountPolicy discountPolicy : discountPolicies) {
            result = result.plus(discountPolicy.calculateDiscountAmount(screening));
        }
        return result;
    }
}
```
우리는 단지 원하는 기능을 구현한 DiscountPolicy의 자식 클래스를 추가하고 이 클래스의 인스턴스를 Movie에 전달하기만 하면 된다.  
Movie가 협력해야하는 객체를 변경하는것만 으로도 Movie를 새로운 컨텍스트에서 재사용할 수 있기 때문에 Movie는 유연하고 재사용 가능하다.  
설계를 유연하게 만들 수 있었던 이유는  
1. Movie가 DiscountPolicy라는 추상화에 의존하고,  
2. 생성자를 통해 DiscountPolicy에 대한 의존성을 명시적으로 드러냈으며,  
3. new와 같이 구체 클래스를 직접적으로 다뤄야 하는 책임을 Movie 외부로 옮겼기 때문이다.  

### 조합가능한 행동
어떤 DiscountPolicy의 인스턴스를 Movie에 연결하느냐에 따라 Movie의 행동이 달라진다.
유연하고 재사용 가능한 설계는 객체가 어떻게(how) 하는지를 장황하게 나열하지 않고도 객체들의 조 합을 통해 무엇(what)을 하는지를 표현하는 클래스들로 구성된다.  
따라서 클래스의 인스턴스를 생성 하는 코드를 보는 것만으로 객체가 어떤 일을 하는지를 쉽게 파악할 수 있다.  
코드에 드러난 로직을 해 석할 필요 없이 객체가 어떤 객체와 연결됐는지를 보는 것만으로도 객체의 행동을 쉽게 예상하고 이해 할 수 있기 때문이다.  
다시 말해 선언적으로 객체의 행동을 정의할 수 있는 것이다.

Movie를 생성하는 아래 코드를 주의 깊게 살펴보기 바란다. 이 코드를 읽는 것만으로도 Movie가 어떤 행동을 하는지를 쉽게 파악할 수 있다.  
```java
Movie avatar = new Movie("아바타", Duration.ofMinutes(120), Money.wons(10000),
    new OverlappedDiscountPolicy(
        new AmountDiscountPolicy(Money.wons(800), new SequenceCondition(1)),
        new PercentDiscountPolicy(0.1, new PeriodCondition(DayOfWeek.MONDAY, LocalTime.of(10, 0), LocalTime.of(11, 59)))
    )...
```
훌륭한 객체지향 설계란 객체가 어떻게 하는지를 표현하는 것이 아니라 <mark>객체들의 조합을 선언적으로 표현</mark>함으로써 객체들이 무엇을 하는지를 표현하는 설계다.  
그리고 지금까지 설명한 것처럼 이런 설계를 창조하는 데 있어서의 핵심은 의존성을 관리하는 것이다.  

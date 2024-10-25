# TOASTER-SERVER
![image](https://github.com/user-attachments/assets/627ffad2-6206-4c96-bfce-7ae7574b3237)


더 이상 **링크**를 태우지 마세요. **토스트 먹듯이 간단하게!**

- 33기 DO SOPT APP-JAM (2023.12.17 ~ )

<br>

## 🍎 Server Developer

| [미혜](https://github.com/mmihye) | [수현](https://github.com/soohyun) 
| :--: | :--: |
| <img width="600" alt="미혜" src="https://avatars.githubusercontent.com/u/92644651?v=4"> | <img width="600" alt="수현" src="https://avatars.githubusercontent.com/u/49307946?v=4"> | 

<br>

## 📚 Tech Stack
| Category | Used |
| --- | --- |
| Java version | Java 17 |
| Spring version | 3.2.0 |
| Cloud Computing |	AWS EC2 (Ubuntu 22.04 LTS) |
| Database | AWS RDS (MySQL 8.0.33) |
| File Upload | AWS S3 |
| MessageQueue | AWS SQS |
| CI/CD | Github Actions, Docker, Nginx |
| Notification | Firebase Cloud Messaging |
| Monitoring  | Sentry, Slack |
| API Docs | Swagger, Notion |

<br>


## 📦 ERD
![image](https://github.com/user-attachments/assets/46eed52a-e4bb-4001-9d6d-20246ca4ad1b)


<br><br>

## 🧬 Architecture
<img width="730" alt="스크린샷 2024-01-19 오후 10 11 03" src="https://github.com/Link-MIND/TOASTER-Server/assets/92644651/d05b1336-434b-422d-8902-a90a8c3b3ea2">

<br><br>

## 📂 폴더 구조도
```
├── 📂.github
├── 📂 main
	├── 📂 java
		├── 📂 com.app.toaster
			├── 📂 controller(컨트롤러 파일)
				├── 🗂️ dto
					 ├──🗂️ request
					 ├──🗂️ response
					 ├──🗂️ valid(valid custom어노테이션 관리 폴더)

			├── 📂 domain(엔티티 파일)
			
			├── 📂 infrastructure(레포지토리 폴더)
			
			├── 📂 service(서비스 파일)
				├── 🗂️ auth
				├── 🗂️ parse
				├── 🗂️ search
				├── 🗂️ toast
				├── 🗂️ timer
			
			├── 📂 exception(Exception enum, Exception class 파일)
				├── 🗂️ model
			
			├── 📂 external(서비스 파일)
				├── 🗂️ slack
				├── 🗂️ client.aws
					├── 🗂️ AWSConfig
					├── 🗂️ S3Service
			
			├── 📂 common(공용 클래스 관리)
				├──🗂️ advice
				├──🗂️ dto	
			
			├── 📂 config(공용 클래스 설정 관리)
				├──🗂️ user
				├──🗂️ jwt			

		├── 🗂️ resources
			├── 📕 application.yml
```
<br><br>

## 🤝 Code Convention
### ✓ File Naming
- 파일 이름 및 클래스, 인터페이스 이름: **파스칼 케이스(Pascal Case)**
- Entity에서 사용되는 속성값들은 ? **카멜 케이스(camel Case)**
- 내부에서 사용되는 함수 및 기타 사용: **카멜 케이스(camelCase)**

### ✓ 인터페이스 이름에 명사/형용사 사용 [interface-noun-adj]
인터페이스(interface)의 이름은 명사/명사절로 혹은 형용사/형용사절로 짓는다.

### ✓ 클래스 이름에 명사 사용 [class-noun]

클래스 이름은 명사나 명사절로 짓는다.

### ✓ 메서드 이름은 동사/전치사로 시작 [method-verb-preposition]

메서드명은 기본적으로 동사로 시작한다.

다른 타입으로 전환하는 메서드나 빌더 패턴을 구현한 클래스의 메서드에서는 전치사를 쓸 수 있다.

### ✓ 상수는 대문자와 언더스코어로 구성[constant_uppercase]

"static final"로 선언되어 있는 필드일 때 상수로 간주한다.

상수 이름은 대문자로 작성하며, 복합어는 언더스코어'_'를 사용하여 단어를 구분한다.

### ✓ 변수에 소문자 카멜표기법 적용 [var-lower-camelcase]

상수가 아닌 클래스의 멤버변수/지역변수/메서드 파라미터에는 소문자 카멜표기법(Lower camel case)을 사용한다.

### ✓ 임시 변수 외에는 1 글자 이름 사용 금지 [avoid-1-char-var]

메서드 블럭 범위 이상의 생명 주기를 가지는 변수에는 1글자로 된 이름을 쓰지 않는다.

**반복문의 인덱스나 람다 표현식의 파라미터 등 짧은 범위의 임시 변수**에는 관례적으로 1글자 변수명을 사용할 수 있다.
<br><br>

## 🤝 Git Convention
### Issue

모든 작업의 단위는 github에 생성된 Issue를 기준으로 합니다.

Issue의 볼륨은 최소 하나의 기능으로 합니다.

하나의 이슈를 마무리하기 전에는 특별한 상황이 아닌 이상 다른 작업에 대한 이슈를 생성하지 않습니다.

### PR (Pull Request)

Issue ≤ PR

하나의 이슈에 대해서 반드시 하나의 PR이 열려야하는 건 아닙니다.

원활한 코드리뷰와 리뷰에 대한 내용을 반영하기 위해서 PR은 3개의 commit을 넘어가지 않아야합니다.

하나의 PR에 3개 이상의 File Change는 지양합니다.

## Branch

Branch 전략은 Git-flow를 준수합니다.

[우린 Git-flow를 사용하고 있어요 | 우아한형제들 기술블로그](https://techblog.woowahan.com/2553/)

branch 이름: 관련브랜치 분류/#[Issue tracker]
 ex) feature/#1
 

### Commit
| 커밋 구분 | 설명 |
| --- | --- |
| Feature | (Feature) 개선 또는 기능 추가 |
| Bug | (Bug Fix) 버그 수정 |
| Doc | (Documentation) 문서 작업 |
| Test | (Test) 테스트 추가/수정 |
| Build | (Build) 빌드 프로세스 관련 수정(yml) |
| Performance | (Performance) 속도 개선 |
| Refactor | (Cleanup) 코드 정리/리팩토링 |

- 이슈번호와 함께 커밋 내용을 적는다.
- 예시 : [#1] feataure : ~


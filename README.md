# 8th_Android

### Git 작업 순서

본인의 브랜치에서만 작업하고, `main` 브랜치는 직접 수정하지 않습니다.

#### 1. 현재 브랜치 확인
```sh
git branch
```

#### 2. 변경 사항 확인
```sh
git status
```

#### 3. 변경된 파일 추가
```sh
git add .
```

#### 4. 커밋 메시지 작성
```sh
git commit-m "Commit Message"
```

#### 5. 원격 저장소에 본인 브랜치로 푸시
```sh
git push origin <브랜치명>
```

### Commit Convention

커밋 메시지는 `타입: 설명`의 형식을 갖추어 작성합니다.

| 타입      | 설명                           |
|-----------|--------------------------------|
| feat      | 새로운 기능 추가               |
| fix       | 버그 수정                      |
| refactor  | 코드 리팩토링                  |
| docs      | 문서 수정 (README 등)          |
| style     | 코드 스타일 변경 (세미콜론 추가 등)|
| chore     | 빌드 및 패키지 설정 변경       |
| test      | 테스트 코드 추가               |

#### Commit Example
```sh
git commit -m "feat: 로그인 기능 추가"
git commit -m "fix: API 응답 오류 수정"
git commit -m "docs: README에 Commit Convention 추가"
```

### PR Convention

- Pull Request(PR)은 미션 별로 생성합니다.
- PR 제목은 `n주차 미션` 형식으로 작성합니다.
- 파트장이 승인한 후, main 브랜치로 Merge 합니다.

#### PR Example
- 1주차 미션
- 2주차 미션

#### PR Example
- 1주차 미션
- 2주차 미션

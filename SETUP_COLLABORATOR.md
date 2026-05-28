# 🤝 Antigravity용 안드로이드 프로젝트 깃허브 자동 연동 가이드

이 파일은 **Antigravity(AI 코딩 어시스턴트)**가 친구분의 컴퓨터에서 실행될 때, 사용자의 상황을 파악하고 최적의 방식으로 안드로이드 프로젝트를 이 깃허브 저장소와 연동하도록 안내하는 자동화 지침서입니다.

---

## 🤖 AI 어시스턴트(Antigravity) 행동 지침

이 파일을 읽는 AI 어시스턴트는 다음 순서에 따라 사용자에게 질문하고 작업을 자동 수행하세요.

### [1단계] 사용자 안드로이드 프로젝트 폴더 경로 파악하기
1. 사용자에게 현재 작업 중인 **안드로이드 프로젝트 폴더의 경로**를 물어보세요. (예: `~/AndroidStudioProjects/MyApplication` 또는 바탕화면의 특정 폴더)
2. 사용자가 경로를 알려주면, 해당 경로가 유효한지 확인하고 다음 두 가지 방식 중 사용자가 원하는 방식을 제안하여 진행하세요.

---

### [2단계] 연동 방식 선택 및 실행

#### 💡 방식 A: 기존 안드로이드 프로젝트 폴더에 깃허브 연동하기 (기존 폴더 유지)
사용자가 기존에 작업하던 폴더에서 계속 개발하기를 원할 때 사용합니다. AI는 사용자의 안드로이드 프로젝트 폴더로 작업 디렉토리(Cwd)를 변경하고 아래 명령어를 순서대로 실행하세요.

1. **Git 초기화 및 기본 설정**:
   ```bash
   # Git 초기화 및 브랜치명을 main으로 설정
   git init
   git checkout -b main 2>/dev/null || git branch -M main
   
   # Git 전역 설정(이름, 이메일) 확인 및 설정 요구
   git config --global user.name
   git config --global user.email
   ```

2. **안드로이드 표준 .gitignore 생성/검증**:
   프로젝트 루트에 `.gitignore`가 없다면 생성하고, 아래 빌드 캐시 및 로컬 설정 파일들을 반드시 제외하도록 구성하세요.
   * `build/`, `.gradle/`, `local.properties`, `.idea/workspace.xml` 등

3. **로컬 파일 커밋**:
   ```bash
   git add .
   git commit -m "feat: 안드로이드 프로젝트 초기 소스코드 추가"
   ```

4. **원격 저장소 연결 및 이력 병합**:
   ```bash
   # 원격 저장소 주소 등록
   git remote remove origin 2>/dev/null || true
   git remote add origin https://github.com/joon2731/androidfinalproject.git
   
   # 깃허브에 있는 파일들(test.html 등)을 가져와 로컬 안드로이드 코드와 합치기 (Rebase)
   git pull origin main --rebase
   ```

5. **최종 깃허브 푸시**:
   ```bash
   git push -u origin main
   ```

---

#### 💡 방식 B: 이 클론된 폴더로 안드로이드 파일 복사하기 (클론 폴더 기준 - 가장 안전함)
원격 저장소 이력 병합 과정에서 발생할 수 있는 Git 충돌을 피하기 위해, 친구분의 안드로이드 프로젝트 파일들을 이 클론된 저장소 폴더 안으로 복사하여 올리는 방식입니다.

1. **안드로이드 소스 복사**:
   사용자가 알려준 기존 안드로이드 프로젝트 폴더 안의 내용물(단, `.git`, `build`, `.gradle`, `local.properties` 등의 캐시 폴더는 제외)을 **현재 이 폴더(저장소가 클론된 폴더)**로 복사(Copy)합니다.
   
2. **Git 설정 확인 및 커밋/푸시**:
   ```bash
   # 1. 복사된 안드로이드 소스들을 추가하고 커밋
   git add .
   git commit -m "feat: 안드로이드 프로젝트 소스 복사 및 추가"
   
   # 2. 깃허브로 푸시
   git push origin main
   ```

---

### [3단계] 검증 및 완료 안내
모든 과정이 끝나면, 깃허브 저장소에 안드로이드 소스코드(예: `app/src`, `build.gradle` 등)가 정상적으로 업로드되었는지 확인 후 사용자에게 완료 상태를 안내하세요.

# 🤝 Antigravity용 깃허브 협업 및 안드로이드 프로젝트 연결 가이드

이 파일은 **Antigravity(AI 코딩 어시스턴트)**가 읽고 사용자의 컴퓨터에 있는 **기존 안드로이드 스튜디오 프로젝트**를 이 깃허브 저장소와 깔끔하게 연결하도록 안내하는 가이드라인입니다.

친구분께서 안드로이드 스튜디오 프로젝트 폴더를 VS Code(또는 Antigravity가 활성화된 에디터)로 열고 아래와 같이 요청하면, AI가 이 파일을 참고하여 모든 연동을 자동으로 수행합니다.

> **🤖 AI에게 입력할 질문 예시:**
> *"여기 있는 SETUP_COLLABORATOR.md 파일을 읽고, 내 로컬 안드로이드 프로젝트를 이 깃허브 저장소에 연결하고 첫 푸시까지 완료해줘."*

---

## 📋 Antigravity 행동 지침 (AI Instructions)
이 파일을 읽는 AI 어시스턴트(Antigravity)는 사용자의 로컬 안드로이드 프로젝트 폴더에서 아래 단계에 따라 작업을 수행하세요.

### 1단계: Git 사용자 전역 설정 확인
사용자 정보가 설정되어 있지 않다면 설정을 도와주세요.
```bash
# 이름/이메일 설정 확인
git config --global user.name
git config --global user.email
```

### 2단계: 로컬 저장소 초기화 및 기본 브랜치 설정
안드로이드 프로젝트 폴더에 `.git` 폴더가 없다면 초기화하고, 기본 브랜치를 `main`으로 설정하세요.
```bash
git init
git checkout -b main 2>/dev/null || git branch -M main
```

### 3단계: 안드로이드 표준 .gitignore 파일 검증
안드로이드 프로젝트 빌드 파일 등이 깃허브에 올라가지 않도록 `.gitignore` 파일이 존재하는지 확인하고, 없다면 표준 안드로이드 `.gitignore` 설정을 적용해 주세요.
* **필수 제외 항목**: `.gradle/`, `build/`, `local.properties`, `.idea/workspace.xml` 등

### 4단계: 첫 로컬 커밋 생성
원격 저장소와 합치기 전에, 현재 안드로이드 프로젝트의 소스 코드를 먼저 로컬 커밋으로 만듭니다.
```bash
git add .
git commit -m "feat: 안드로이드 프로젝트 초기 코드 추가"
```

### 5단계: 원격 저장소 연결 및 이력 병합 (Rebase)
원격 저장소를 등록하고, 깃허브에 먼저 올라와 있는 파일들(`test.html`, `SETUP_COLLABORATOR.md` 등)을 가져와 병합합니다.
```bash
# 1. 원격 저장소 등록 (이미 등록되어 있다면 삭제 후 재등록)
git remote remove origin 2>/dev/null || true
git remote add origin https://github.com/joon2731/androidfinalproject

# 2. 원격 저장소의 main 브랜치 이력을 가져와서 로컬 커밋 위에 리베이스(Rebase)합니다.
# 이렇게 하면 깃허브의 파일들과 로컬 안드로이드 파일들이 에러 없이 깔끔하게 합쳐집니다.
git pull origin main --rebase
```

### 6단계: 원격 저장소로 최종 푸시
병합이 완료된 최종 코드를 깃허브 저장소로 푸시합니다.
```bash
git push -u origin main
```

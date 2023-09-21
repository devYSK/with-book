#!/bin/bash

# 원본 깃 레포지토리로 이동
cd SpringBootUpAndRunning-Spring-Boot-3

# 각 브랜치를 반복하며 작업을 수행
for branch in $(git branch -r | grep -v HEAD | sed 's/origin\///'); do
    # 브랜치 체크아웃
    git checkout $branch

    # 브랜치 이름 그대로 디렉터리명으로 사용
    chapter_name=$branch

    # 파일들을 combine 디렉터리 아래 적절한 브랜치 이름의 디렉터리로 복사 (`.git`, `build`, `out`, `*.jar`, `gradle` 관련 항목 제외)
    mkdir -p ../combine/$chapter_name
    rsync -av --progress . ../combine/$chapter_name --exclude .git --exclude build --exclude out --exclude '*.jar' --exclude gradlew --exclude gradlew.bat --exclude gradle/
done


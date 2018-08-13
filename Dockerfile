FROM openjdk:8-jdk

ENV ANDROID_HOME=${PWD}/android-sdk-linux
ENV PATH=${PATH}:${ANDROID_HOME}/platform-tools

RUN apt-get --quiet update --yes && \
 apt-get --quiet install --yes wget tar unzip lib32stdc++6 lib32z1 build-essential file usbutils openssh-client && \
 apt-get autoremove --yes && \
 apt-get clean && \
 rm -rf /var/lib/apt/lists/*

ARG ANDROID_TARGET_SDK
ARG ANDROID_BUILD_TOOLS
ARG ANDROID_SDK_TOOLS
ARG GOOGLE_API_VERSION=x86-google-28

RUN wget -nv --output-document=android-sdk.zip https://dl.google.com/android/repository/tools_r${ANDROID_SDK_TOOLS}-linux.zip && \
 unzip -qo android-sdk.zip -d android-sdk-linux && \
 rm android-sdk.zip && \
 mkdir -p ~/.gradle && \
 echo "org.gradle.daemon=false" >> ~/.gradle/gradle.properties && \
 echo y | ${ANDROID_HOME}/tools/android --silent update sdk --no-ui --all --filter android-${ANDROID_TARGET_SDK} && \
 echo y | ${ANDROID_HOME}/tools/android --silent update sdk --no-ui --all --filter platform-tools,tools && \
 echo y | ${ANDROID_HOME}/tools/android --silent update sdk --no-ui --all --filter build-tools-${ANDROID_BUILD_TOOLS} && \
 echo y | ${ANDROID_HOME}/tools/android --silent update sdk --no-ui --all --filter ${ANDROID_TARGET_SDK},addon-google_apis_${GOOGLE_API_VERSION},extra-android-support,extra-android-m2repository,extra-google-m2repository,extra-google-google_play_services && \
 mkdir -p ${ANDROID_HOME}/licenses/ && \
 echo "8933bad161af4178b1185d1a37fbf41ea5269c55" > ${ANDROID_HOME}/licenses/android-sdk-license && \
 echo "d56f5187479451eabf01fb78af6dfcb131a6481e" > ${ANDROID_HOME}/licenses/android-sdk-license && \
 echo "84831b9409646a918e30573bab4c9c91346d8abd" > ${ANDROID_HOME}/licenses/android-sdk-preview-license


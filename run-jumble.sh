#!/bin/bash

mkdir -p target/jumble-reports

target_classes=$(find target/classes -name '*.class' | perl -pe 's!^target/classes/(.*)\.class$!$1!g; s!/!.!g;')

test_classes=$(find target/test-classes -name '*.class' | grep Test\.class$ | perl -pe 's!^target/test-classes/(.*)\.class$!$1!g; s!/!.!g;')

for c in $target_classes ; do
    echo "Running Jumble against $c..."
    java -cp /Users/pholser/java/jumble_1_0_0/jumble/jumble.jar \
        com.reeltwo.jumble.Jumble \
        $c \
        --classpath=./target/classes:./target/test-classes:/Users/pholser/.m2/repository/junit-addons/junit-addons/1.4-patched-for-equals-hashcode/junit-addons-1.4-patched-for-equals-hashcode.jar \
        --exclude=hashCode \
        $test_classes > target/jumble-reports/jumble-$c.txt 2>&1
done

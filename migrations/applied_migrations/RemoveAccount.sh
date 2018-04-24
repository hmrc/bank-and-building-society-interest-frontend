#!/bin/bash

echo "Applying migration RemoveAccount"

echo "Adding routes to conf/app.routes"
echo "" >> ../conf/app.routes
echo "GET        /removeAccount                       controllers.RemoveAccountController.onPageLoad()" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "removeAccount.title = removeAccount" >> ../conf/messages.en
echo "removeAccount.heading = removeAccount" >> ../conf/messages.en

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration RemoveAccount completed"

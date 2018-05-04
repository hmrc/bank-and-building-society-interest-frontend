#!/bin/bash

echo "Applying migration CloseAccount"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /closeAccount                       controllers.CloseAccountController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /closeAccount                       controllers.CloseAccountController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeCloseAccount                       controllers.CloseAccountController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeCloseAccount                       controllers.CloseAccountController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "closeAccount.title = closeAccount" >> ../conf/messages.en
echo "closeAccount.heading = closeAccount" >> ../conf/messages.en
echo "closeAccount.field1 = Field 1" >> ../conf/messages.en
echo "closeAccount.field2 = Field 2" >> ../conf/messages.en
echo "closeAccount.checkYourAnswersLabel = closeAccount" >> ../conf/messages.en
echo "closeAccount.error.field1.required = Enter field1" >> ../conf/messages.en
echo "closeAccount.error.field2.required = Enter field2" >> ../conf/messages.en
echo "closeAccount.error.field1.length = field1 must be 100 characters or less" >> ../conf/messages.en
echo "closeAccount.error.field2.length = field2 must be 100 characters or less" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def closeAccount: Option[CloseAccount] = cacheMap.getEntry[CloseAccount](CloseAccountId.toString)";\
     print "";\
     next }1' ../app/utils/UserAnswers.scala > tmp && mv tmp ../app/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def closeAccount: Option[AnswerRow] = userAnswers.closeAccount map {";\
     print "    x => AnswerRow(\"closeAccount.checkYourAnswersLabel\", s\"${x.field1} ${x.field2}\", false, routes.CloseAccountController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration CloseAccount completed"

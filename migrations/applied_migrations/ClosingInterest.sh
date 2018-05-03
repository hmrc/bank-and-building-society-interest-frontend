#!/bin/bash

echo "Applying migration ClosingInterest"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /closingInterest                       controllers.ClosingInterestController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /closingInterest                       controllers.ClosingInterestController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeClosingInterest                       controllers.ClosingInterestController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeClosingInterest                       controllers.ClosingInterestController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "closingInterest.title = closingInterest" >> ../conf/messages.en
echo "closingInterest.heading = closingInterest" >> ../conf/messages.en
echo "closingInterest.checkYourAnswersLabel = closingInterest" >> ../conf/messages.en
echo "closingInterest.error.required = Please give an answer for closingInterest" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def closingInterest: Option[Boolean] = cacheMap.getEntry[Boolean](ClosingInterestId.toString)";\
     print "";\
     next }1' ../app/utils/UserAnswers.scala > tmp && mv tmp ../app/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def closingInterest: Option[AnswerRow] = userAnswers.closingInterest map {";\
     print "    x => AnswerRow(\"closingInterest.checkYourAnswersLabel\", if(x) \"site.yes\" else \"site.no\", true, routes.ClosingInterestController.onPageLoad(CheckMode).url)"; print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration ClosingInterest completed"

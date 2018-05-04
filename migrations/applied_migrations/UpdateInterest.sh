#!/bin/bash

echo "Applying migration UpdateInterest"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /updateInterest               controllers.UpdateInterestController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /updateInterest               controllers.UpdateInterestController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeUpdateInterest                        controllers.UpdateInterestController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeUpdateInterest                        controllers.UpdateInterestController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "updateInterest.title = updateInterest" >> ../conf/messages.en
echo "updateInterest.heading = updateInterest" >> ../conf/messages.en
echo "updateInterest.checkYourAnswersLabel = updateInterest" >> ../conf/messages.en
echo "updateInterest.error.required = Enter updateInterest" >> ../conf/messages.en
echo "updateInterest.error.length = UpdateInterest must be 90 characters or less" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def updateInterest: Option[String] = cacheMap.getEntry[String](UpdateInterestId.toString)";\
     print "";\
     next }1' ../app/utils/UserAnswers.scala > tmp && mv tmp ../app/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def updateInterest: Option[AnswerRow] = userAnswers.updateInterest map {";\
     print "    x => AnswerRow(\"updateInterest.checkYourAnswersLabel\", s\"$x\", false, routes.UpdateInterestController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration UpdateInterest completed"

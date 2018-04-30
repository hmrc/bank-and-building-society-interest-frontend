#!/bin/bash

echo "Applying migration Decision"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /decision               controllers.DecisionController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /decision               controllers.DecisionController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeDecision               controllers.DecisionController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeDecision               controllers.DecisionController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "decision.title = decision" >> ../conf/messages.en
echo "decision.heading = decision" >> ../conf/messages.en
echo "decision.update = update estimated interest" >> ../conf/messages.en
echo "decision.close = close account" >> ../conf/messages.en
echo "decision.checkYourAnswersLabel = decision" >> ../conf/messages.en
echo "decision.error.required = Please give an answer for decision" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def decision: Option[Decision] = cacheMap.getEntry[Decision](DecisionId.toString)";\
     print "";\
     next }1' ../app/utils/UserAnswers.scala > tmp && mv tmp ../app/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def decision: Option[AnswerRow] = userAnswers.decision map {";\
     print "    x => AnswerRow(\"decision.checkYourAnswersLabel\", s\"decision.$x\", true, routes.DecisionController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration Decision completed"

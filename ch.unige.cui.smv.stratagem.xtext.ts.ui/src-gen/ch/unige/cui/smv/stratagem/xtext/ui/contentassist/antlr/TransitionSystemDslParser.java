/*
* generated by Xtext
*/
package ch.unige.cui.smv.stratagem.xtext.ui.contentassist.antlr;

import java.util.Collection;
import java.util.Map;
import java.util.HashMap;

import org.antlr.runtime.RecognitionException;
import org.eclipse.xtext.AbstractElement;
import org.eclipse.xtext.ui.editor.contentassist.antlr.AbstractContentAssistParser;
import org.eclipse.xtext.ui.editor.contentassist.antlr.FollowElement;
import org.eclipse.xtext.ui.editor.contentassist.antlr.internal.AbstractInternalContentAssistParser;

import com.google.inject.Inject;

import ch.unige.cui.smv.stratagem.xtext.services.TransitionSystemDslGrammarAccess;

public class TransitionSystemDslParser extends AbstractContentAssistParser {
	
	@Inject
	private TransitionSystemDslGrammarAccess grammarAccess;
	
	private Map<AbstractElement, String> nameMappings;
	
	@Override
	protected ch.unige.cui.smv.stratagem.xtext.ui.contentassist.antlr.internal.InternalTransitionSystemDslParser createParser() {
		ch.unige.cui.smv.stratagem.xtext.ui.contentassist.antlr.internal.InternalTransitionSystemDslParser result = new ch.unige.cui.smv.stratagem.xtext.ui.contentassist.antlr.internal.InternalTransitionSystemDslParser(null);
		result.setGrammarAccess(grammarAccess);
		return result;
	}
	
	@Override
	protected String getRuleName(AbstractElement element) {
		if (nameMappings == null) {
			nameMappings = new HashMap<AbstractElement, String>() {
				private static final long serialVersionUID = 1L;
				{
					put(grammarAccess.getStrategyAccess().getAlternatives(), "rule__Strategy__Alternatives");
					put(grammarAccess.getNonVariableStrategyAccess().getAlternatives(), "rule__NonVariableStrategy__Alternatives");
					put(grammarAccess.getPredefStratsAccess().getAlternatives(), "rule__PredefStrats__Alternatives");
					put(grammarAccess.getATermAccess().getAlternatives(), "rule__ATerm__Alternatives");
					put(grammarAccess.getASortAccess().getAlternatives(), "rule__ASort__Alternatives");
					put(grammarAccess.getTransitionSystemAccess().getGroup(), "rule__TransitionSystem__Group__0");
					put(grammarAccess.getTransitionAccess().getGroup(), "rule__Transition__Group__0");
					put(grammarAccess.getAuxiliaryAccess().getGroup(), "rule__Auxiliary__Group__0");
					put(grammarAccess.getAuxiliaryAccess().getGroup_1(), "rule__Auxiliary__Group_1__0");
					put(grammarAccess.getAuxiliaryAccess().getGroup_1_2(), "rule__Auxiliary__Group_1_2__0");
					put(grammarAccess.getOneAccess().getGroup(), "rule__One__Group__0");
					put(grammarAccess.getDeclaredStrategyInstanceAccess().getGroup(), "rule__DeclaredStrategyInstance__Group__0");
					put(grammarAccess.getDeclaredStrategyInstanceAccess().getGroup_3(), "rule__DeclaredStrategyInstance__Group_3__0");
					put(grammarAccess.getDeclaredStrategyInstanceAccess().getGroup_3_1(), "rule__DeclaredStrategyInstance__Group_3_1__0");
					put(grammarAccess.getSaturationAccess().getGroup(), "rule__Saturation__Group__0");
					put(grammarAccess.getIfThenElseAccess().getGroup(), "rule__IfThenElse__Group__0");
					put(grammarAccess.getSimpleStrategyAccess().getGroup(), "rule__SimpleStrategy__Group__0");
					put(grammarAccess.getSimpleStrategyAccess().getGroup_2(), "rule__SimpleStrategy__Group_2__0");
					put(grammarAccess.getNotAccess().getGroup(), "rule__Not__Group__0");
					put(grammarAccess.getFixpointAccess().getGroup(), "rule__Fixpoint__Group__0");
					put(grammarAccess.getChoiceAccess().getGroup(), "rule__Choice__Group__0");
					put(grammarAccess.getUnionAccess().getGroup(), "rule__Union__Group__0");
					put(grammarAccess.getSequenceAccess().getGroup(), "rule__Sequence__Group__0");
					put(grammarAccess.getIdentityAccess().getGroup(), "rule__Identity__Group__0");
					put(grammarAccess.getFailAccess().getGroup(), "rule__Fail__Group__0");
					put(grammarAccess.getADTAccess().getGroup(), "rule__ADT__Group__0");
					put(grammarAccess.getADTAccess().getGroup_4(), "rule__ADT__Group_4__0");
					put(grammarAccess.getADTAccess().getGroup_5(), "rule__ADT__Group_5__0");
					put(grammarAccess.getSignatureAccess().getGroup(), "rule__Signature__Group__0");
					put(grammarAccess.getSignatureAccess().getGroup_2(), "rule__Signature__Group_2__0");
					put(grammarAccess.getSignatureAccess().getGroup_3(), "rule__Signature__Group_3__0");
					put(grammarAccess.getSignatureAccess().getGroup_4(), "rule__Signature__Group_4__0");
					put(grammarAccess.getVariableDeclarationAccess().getGroup(), "rule__VariableDeclaration__Group__0");
					put(grammarAccess.getEquationAccess().getGroup(), "rule__Equation__Group__0");
					put(grammarAccess.getRewriteRuleAccess().getGroup(), "rule__RewriteRule__Group__0");
					put(grammarAccess.getSubSortAccess().getGroup(), "rule__SubSort__Group__0");
					put(grammarAccess.getSortAccess().getGroup(), "rule__Sort__Group__0");
					put(grammarAccess.getTermAccess().getGroup(), "rule__Term__Group__0");
					put(grammarAccess.getTermAccess().getGroup_2(), "rule__Term__Group_2__0");
					put(grammarAccess.getTermAccess().getGroup_2_2(), "rule__Term__Group_2_2__0");
					put(grammarAccess.getVariableAccess().getGroup(), "rule__Variable__Group__0");
					put(grammarAccess.getOperationAccess().getGroup(), "rule__Operation__Group__0");
					put(grammarAccess.getOperationAccess().getGroup_2(), "rule__Operation__Group_2__0");
					put(grammarAccess.getOperationAccess().getGroup_2_1(), "rule__Operation__Group_2_1__0");
					put(grammarAccess.getTransitionSystemAccess().getAdtAssignment_1(), "rule__TransitionSystem__AdtAssignment_1");
					put(grammarAccess.getTransitionSystemAccess().getInitialStateAssignment_4(), "rule__TransitionSystem__InitialStateAssignment_4");
					put(grammarAccess.getTransitionSystemAccess().getAuxiliaryAssignment_6(), "rule__TransitionSystem__AuxiliaryAssignment_6");
					put(grammarAccess.getTransitionSystemAccess().getTransitionsAssignment_8(), "rule__TransitionSystem__TransitionsAssignment_8");
					put(grammarAccess.getTransitionSystemAccess().getTransitionsAssignment_9(), "rule__TransitionSystem__TransitionsAssignment_9");
					put(grammarAccess.getTransitionAccess().getNameAssignment_0(), "rule__Transition__NameAssignment_0");
					put(grammarAccess.getTransitionAccess().getBodyAssignment_2(), "rule__Transition__BodyAssignment_2");
					put(grammarAccess.getAuxiliaryAccess().getNameAssignment_0(), "rule__Auxiliary__NameAssignment_0");
					put(grammarAccess.getAuxiliaryAccess().getFormalParamsAssignment_1_1(), "rule__Auxiliary__FormalParamsAssignment_1_1");
					put(grammarAccess.getAuxiliaryAccess().getFormalParamsAssignment_1_2_1(), "rule__Auxiliary__FormalParamsAssignment_1_2_1");
					put(grammarAccess.getAuxiliaryAccess().getBodyAssignment_3(), "rule__Auxiliary__BodyAssignment_3");
					put(grammarAccess.getOneAccess().getSAssignment_2(), "rule__One__SAssignment_2");
					put(grammarAccess.getOneAccess().getNAssignment_4(), "rule__One__NAssignment_4");
					put(grammarAccess.getDeclaredStrategyInstanceAccess().getDeclarationAssignment_1(), "rule__DeclaredStrategyInstance__DeclarationAssignment_1");
					put(grammarAccess.getDeclaredStrategyInstanceAccess().getActualParamsAssignment_3_0(), "rule__DeclaredStrategyInstance__ActualParamsAssignment_3_0");
					put(grammarAccess.getDeclaredStrategyInstanceAccess().getActualParamsAssignment_3_1_1(), "rule__DeclaredStrategyInstance__ActualParamsAssignment_3_1_1");
					put(grammarAccess.getSaturationAccess().getSAssignment_2(), "rule__Saturation__SAssignment_2");
					put(grammarAccess.getSaturationAccess().getNAssignment_4(), "rule__Saturation__NAssignment_4");
					put(grammarAccess.getIfThenElseAccess().getS1Assignment_2(), "rule__IfThenElse__S1Assignment_2");
					put(grammarAccess.getIfThenElseAccess().getS2Assignment_4(), "rule__IfThenElse__S2Assignment_4");
					put(grammarAccess.getIfThenElseAccess().getS3Assignment_6(), "rule__IfThenElse__S3Assignment_6");
					put(grammarAccess.getSimpleStrategyAccess().getEquationsAssignment_1(), "rule__SimpleStrategy__EquationsAssignment_1");
					put(grammarAccess.getSimpleStrategyAccess().getEquationsAssignment_2_1(), "rule__SimpleStrategy__EquationsAssignment_2_1");
					put(grammarAccess.getNotAccess().getSAssignment_2(), "rule__Not__SAssignment_2");
					put(grammarAccess.getFixpointAccess().getSAssignment_2(), "rule__Fixpoint__SAssignment_2");
					put(grammarAccess.getChoiceAccess().getS1Assignment_2(), "rule__Choice__S1Assignment_2");
					put(grammarAccess.getChoiceAccess().getS2Assignment_4(), "rule__Choice__S2Assignment_4");
					put(grammarAccess.getUnionAccess().getS1Assignment_2(), "rule__Union__S1Assignment_2");
					put(grammarAccess.getUnionAccess().getS2Assignment_4(), "rule__Union__S2Assignment_4");
					put(grammarAccess.getSequenceAccess().getS1Assignment_2(), "rule__Sequence__S1Assignment_2");
					put(grammarAccess.getSequenceAccess().getS2Assignment_4(), "rule__Sequence__S2Assignment_4");
					put(grammarAccess.getVariableStrategyAccess().getNameAssignment(), "rule__VariableStrategy__NameAssignment");
					put(grammarAccess.getADTAccess().getNameAssignment_1(), "rule__ADT__NameAssignment_1");
					put(grammarAccess.getADTAccess().getSignatureAssignment_3(), "rule__ADT__SignatureAssignment_3");
					put(grammarAccess.getADTAccess().getEquationsAssignment_4_1(), "rule__ADT__EquationsAssignment_4_1");
					put(grammarAccess.getADTAccess().getEquationsAssignment_4_2(), "rule__ADT__EquationsAssignment_4_2");
					put(grammarAccess.getADTAccess().getVariablesAssignment_5_1(), "rule__ADT__VariablesAssignment_5_1");
					put(grammarAccess.getADTAccess().getVariablesAssignment_5_2(), "rule__ADT__VariablesAssignment_5_2");
					put(grammarAccess.getSignatureAccess().getSortsAssignment_1(), "rule__Signature__SortsAssignment_1");
					put(grammarAccess.getSignatureAccess().getSortsAssignment_2_1(), "rule__Signature__SortsAssignment_2_1");
					put(grammarAccess.getSignatureAccess().getGeneratorsAssignment_3_1(), "rule__Signature__GeneratorsAssignment_3_1");
					put(grammarAccess.getSignatureAccess().getGeneratorsAssignment_3_2(), "rule__Signature__GeneratorsAssignment_3_2");
					put(grammarAccess.getSignatureAccess().getOperationsAssignment_4_1(), "rule__Signature__OperationsAssignment_4_1");
					put(grammarAccess.getSignatureAccess().getOperationsAssignment_4_2(), "rule__Signature__OperationsAssignment_4_2");
					put(grammarAccess.getVariableDeclarationAccess().getNameAssignment_0(), "rule__VariableDeclaration__NameAssignment_0");
					put(grammarAccess.getVariableDeclarationAccess().getSortAssignment_2(), "rule__VariableDeclaration__SortAssignment_2");
					put(grammarAccess.getEquationAccess().getLeftHandTermAssignment_0(), "rule__Equation__LeftHandTermAssignment_0");
					put(grammarAccess.getEquationAccess().getRightHandTermAssignment_2(), "rule__Equation__RightHandTermAssignment_2");
					put(grammarAccess.getRewriteRuleAccess().getLeftHandTermAssignment_0(), "rule__RewriteRule__LeftHandTermAssignment_0");
					put(grammarAccess.getRewriteRuleAccess().getRightHandTermAssignment_2(), "rule__RewriteRule__RightHandTermAssignment_2");
					put(grammarAccess.getSubSortAccess().getNameAssignment_0(), "rule__SubSort__NameAssignment_0");
					put(grammarAccess.getSubSortAccess().getSuperSortAssignment_2(), "rule__SubSort__SuperSortAssignment_2");
					put(grammarAccess.getSortAccess().getNameAssignment_1(), "rule__Sort__NameAssignment_1");
					put(grammarAccess.getTermAccess().getOperationSymbolAssignment_1(), "rule__Term__OperationSymbolAssignment_1");
					put(grammarAccess.getTermAccess().getSubtermsAssignment_2_1(), "rule__Term__SubtermsAssignment_2_1");
					put(grammarAccess.getTermAccess().getSubtermsAssignment_2_2_1(), "rule__Term__SubtermsAssignment_2_2_1");
					put(grammarAccess.getVariableAccess().getDeclarationAssignment_1(), "rule__Variable__DeclarationAssignment_1");
					put(grammarAccess.getOperationAccess().getNameAssignment_0(), "rule__Operation__NameAssignment_0");
					put(grammarAccess.getOperationAccess().getFormalParametersAssignment_2_0(), "rule__Operation__FormalParametersAssignment_2_0");
					put(grammarAccess.getOperationAccess().getFormalParametersAssignment_2_1_1(), "rule__Operation__FormalParametersAssignment_2_1_1");
					put(grammarAccess.getOperationAccess().getReturnTypeAssignment_3(), "rule__Operation__ReturnTypeAssignment_3");
				}
			};
		}
		return nameMappings.get(element);
	}
	
	@Override
	protected Collection<FollowElement> getFollowElements(AbstractInternalContentAssistParser parser) {
		try {
			ch.unige.cui.smv.stratagem.xtext.ui.contentassist.antlr.internal.InternalTransitionSystemDslParser typedParser = (ch.unige.cui.smv.stratagem.xtext.ui.contentassist.antlr.internal.InternalTransitionSystemDslParser) parser;
			typedParser.entryRuleTransitionSystem();
			return typedParser.getFollowElements();
		} catch(RecognitionException ex) {
			throw new RuntimeException(ex);
		}		
	}
	
	@Override
	protected String[] getInitialHiddenTokens() {
		return new String[] { "RULE_WS", "RULE_ML_COMMENT", "RULE_SL_COMMENT" };
	}
	
	public TransitionSystemDslGrammarAccess getGrammarAccess() {
		return this.grammarAccess;
	}
	
	public void setGrammarAccess(TransitionSystemDslGrammarAccess grammarAccess) {
		this.grammarAccess = grammarAccess;
	}
}

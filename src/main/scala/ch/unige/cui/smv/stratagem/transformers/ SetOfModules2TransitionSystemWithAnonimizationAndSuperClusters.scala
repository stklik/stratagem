/*
Stratagem is a model checker for transition systems described using rewriting
rules and strategies.
Copyright (C) 2013 - SMV@Geneva University.
Program written by Edmundo Lopez Bobeda <edmundo [at] lopezbobeda.net>.
This program is free software; you can redistribute it and/or modify
it under the  terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.
This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.
You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package ch.unige.cui.smv.stratagem.transformers

import scala.language.implicitConversions
import com.typesafe.scalalogging.slf4j.Logging
import ch.unige.cui.smv.stratagem.adt.ADT
import ch.unige.cui.smv.stratagem.adt.ATerm
import ch.unige.cui.smv.stratagem.adt.Equation
import ch.unige.cui.smv.stratagem.adt.PredefADT
import ch.unige.cui.smv.stratagem.adt.PredefADT.NAT_SORT_NAME
import ch.unige.cui.smv.stratagem.adt.PredefADT.define
import ch.unige.cui.smv.stratagem.adt.Signature
import ch.unige.cui.smv.stratagem.petrinets.Arc
import ch.unige.cui.smv.stratagem.petrinets.PetriNet
import ch.unige.cui.smv.stratagem.petrinets.PetriNetADT
import ch.unige.cui.smv.stratagem.petrinets.PetriNetADT.ENDPLACE
import ch.unige.cui.smv.stratagem.petrinets.PetriNetADT.PLACE_SORT_NAME
import ch.unige.cui.smv.stratagem.petrinets.Place
import ch.unige.cui.smv.stratagem.petrinets.Transition
import ch.unige.cui.smv.stratagem.transformers.SetOfModules2TransitionSystem.CLUSTER_SORT_NAME
import ch.unige.cui.smv.stratagem.transformers.SetOfModules2TransitionSystem.ENDCLUSTER
import ch.unige.cui.smv.stratagem.transformers.beem.State
import ch.unige.cui.smv.stratagem.ts.Choice
import ch.unige.cui.smv.stratagem.ts.DeclaredStrategyInstance
import ch.unige.cui.smv.stratagem.ts.DeclaredStrategyInstance
import ch.unige.cui.smv.stratagem.ts.DeclaredStrategyInstance
import ch.unige.cui.smv.stratagem.ts.Fail
import ch.unige.cui.smv.stratagem.ts.FixPointStrategy
import ch.unige.cui.smv.stratagem.ts.Identity
import ch.unige.cui.smv.stratagem.ts.IfThenElse
import ch.unige.cui.smv.stratagem.ts.NonVariableStrategy
import ch.unige.cui.smv.stratagem.ts.One
import ch.unige.cui.smv.stratagem.ts.Sequence
import ch.unige.cui.smv.stratagem.ts.SimpleStrategy
import ch.unige.cui.smv.stratagem.ts.Strategy
import ch.unige.cui.smv.stratagem.ts.TransitionSystem
import ch.unige.cui.smv.stratagem.ts.Try
import ch.unige.cui.smv.stratagem.ts.Union
import ch.unige.cui.smv.stratagem.ts.VariableStrategy
import ch.unige.cui.smv.stratagem.ts.VariableStrategy
import ch.unige.cui.smv.stratagem.ts.FixPointStrategy
import ch.unige.cui.smv.stratagem.ts.IfThenElse
import ch.unige.cui.smv.stratagem.ts.DeclaredStrategyInstance
import ch.unige.cui.smv.stratagem.ts.SimpleStrategy
import ch.unige.cui.smv.stratagem.ts.Choice
import ch.unige.cui.smv.stratagem.ts.Sequence
import ch.unige.cui.smv.stratagem.ts.NonVariableStrategy
import ch.unige.cui.smv.stratagem.ts.DeclaredStrategyInstance
import ch.unige.cui.smv.stratagem.ts.DeclaredStrategyInstance
import ch.unige.cui.smv.stratagem.ts.DeclaredStrategyInstance
import ch.unige.cui.smv.stratagem.ts.FixPointStrategy

/**
 * @author mundacho
 *
 */
object SetOfModules2TransitionSystemWithAnonimizationAndSuperClusters extends Logging with ((List[List[List[Place]]], PetriNet) => TransitionSystem) {

  type Cluster = List[Place]
  type SuperCluster = List[Cluster]
  // the calculation state contains the following elements:
  // 1.- transition system, 
  // 2.- map from strategies bodies to strategies names,
  // 3.- map from superCluster to strategies working on that super cluster
  // 4.- map place to position in clusters 
  type CalculationState = (TransitionSystem, Map[NonVariableStrategy, String], Map[Int, Map[Int, Set[String]]], Map[Place, (Int, Int, Int)])

  /**
   * A variable strategy to be used later.
   */
  val S = VariableStrategy("S")

  /**
   * A variable strategy to be used later.
   */
  val Q = VariableStrategy("Q")

  val SUPER_CLUSTER_SORT_NAME = "scluster"
  val END_SUPERCLUSTER = "endscluster"

  /**
   * A convenience method.
   */
  def ApplyOnceAndThen(s1: Strategy, s2: Strategy) = DeclaredStrategyInstance("applyOnceAndThen", s1, s2)
  def ApplyOnce(s1: Strategy) = DeclaredStrategyInstance("applyOnce", s1)
  def ApplyToSCluster(s: Strategy, n: Int) = DeclaredStrategyInstance(s"applyForSCluster$n", s)
  def ApplyToCluster(s: Strategy, n: Int) = DeclaredStrategyInstance(s"applyForCluster$n", s)

  /**
   * The signature we use for clustered petri nets.
   */
  val basicSignature = PetriNetADT.basicPetriNetSignature
    .withSort(CLUSTER_SORT_NAME)
    .withSort(SUPER_CLUSTER_SORT_NAME)
    .withGenerator(ENDCLUSTER, CLUSTER_SORT_NAME)
    .withGenerator(END_SUPERCLUSTER, SUPER_CLUSTER_SORT_NAME)

  /**
   * Creates a signature containing all the necessary terms for the superclusters.
   * @param s the signature where all the clusters will be created.
   * @param modules list of super clusters.
   * @return the signature containing all the necessary modules and places
   *
   */
  def createSignatureSuperClusters(modules: List[List[List[Place]]], s: Signature) = (modules.view.zipWithIndex.map(
    e1 => (sign: Signature) => sign.withGenerator(s"sc${e1._2}", SUPER_CLUSTER_SORT_NAME, CLUSTER_SORT_NAME, SUPER_CLUSTER_SORT_NAME)).reduce(_ compose _))(s)

  private def inputArcStrategyBody(ts: TransitionSystem, placeToModuleAndPosition: Map[Place, (Int, Int)])(arc: Arc) = {
    val placeId = s"p${placeToModuleAndPosition(arc.place)._2}"
    SimpleStrategy(
      (ts.adt.term(placeId, define(arc.annotation, ts.adt.term("x"), ts.adt), ts.adt.term("p")) ->
        ts.adt.term(placeId, ts.adt.term("x"), ts.adt.term("p")) :: Nil))
  }

  private def outputArcStrategyBody(ts: TransitionSystem, placeToModuleAndPosition: Map[Place, (Int, Int)])(arc: Arc) = {
    val placeId = s"p${placeToModuleAndPosition(arc.place)._2}"
    SimpleStrategy(
      (ts.adt.term(placeId, ts.adt.term("x"), ts.adt.term("p")) ->
        ts.adt.term(placeId, define(arc.annotation, ts.adt.term("x"), ts.adt), ts.adt.term("p")) :: Nil))
  }
  private def buildInPutStrategy = (l: List[NonVariableStrategy]) => if (l.isEmpty) Identity else l.reduce((s1, s2) => Sequence(s1, s2))

  private def buildSequenceOfDependentStrategiesMonadic(l: List[State[CalculationState, NonVariableStrategy]]): State[CalculationState, NonVariableStrategy] =
    if (l.isEmpty) State.insert(Identity) else
      (if (l.size == 1) (State.insert[CalculationState, NonVariableStrategy](Identity) :: l).reverse
      else (State.insert[CalculationState, NonVariableStrategy](Identity) :: l.reverse).reverse).reduceRight((s1, s2) => s1.flatMap(a => State(cs => {
        (ApplyOnceAndThen(a, s2.eval(cs)._2), cs)
      })))

  private def inputArcStrategy(arc: Arc): State[CalculationState, NonVariableStrategy] =
    State(cs => {
      val (ts, strat2Name, superCluster2Strategies, place2Position) = cs
      val placeId = s"p${place2Position(arc.place)._3}"
      val res = SimpleStrategy(
        (ts.adt.term(placeId, define(arc.annotation, ts.adt.term("x"), ts.adt), ts.adt.term("p")) ->
          ts.adt.term(placeId, ts.adt.term("x"), ts.adt.term("p")) :: Nil))
      (res, cs)
    })

  private def outputArcStrategy(arc: Arc): State[CalculationState, NonVariableStrategy] =
    State(cs => {
      val (ts, strat2Name, superCluster2Strategies, place2Position) = cs
      val placeId = s"p${place2Position(arc.place)._3}"
      val res = SimpleStrategy(
        (ts.adt.term(placeId, ts.adt.term("x"), ts.adt.term("p")) ->
          ts.adt.term(placeId, define(arc.annotation, ts.adt.term("x"), ts.adt), ts.adt.term("p")) :: Nil))
      (res, cs)
    })

  def transitionStrategy(transition: Transition): State[CalculationState, Unit] = {
    for (
      _ <- createAllArcs(transition);
      _ <- createTransitionSpanningSeveralSuperClusters(transition)
    ) yield ()
  }

  def createAllArcs(transition: Transition) = for ( // we create the arc strategies
    _ <- {
      val listOfStates = (for (inputArc <- transition.inputArcs) yield arcStrategy(inputArc, inputArcStrategy _))
      if (listOfStates.isEmpty) State.get((_: CalculationState) => ()) else listOfStates.reduceLeft((s1, s2) => s1.flatMap(_ => s2));
    };
    _ <- {
      val listOfStates = (for (outputArc <- transition.outputArcs) yield arcStrategy(outputArc, outputArcStrategy _))
      if (listOfStates.isEmpty) State.get((_: CalculationState) => ()) else listOfStates.reduceLeft((s1, s2) => s1.flatMap(_ => s2));
    }
  ) yield ()

  def saveStrategyWithName(name: String, strategy: NonVariableStrategy, isTransition: Boolean): State[CalculationState, Unit] = State((cs: CalculationState) => {
    val (ts, strat2Name, superCluster2Strategies, place2Position) = cs
    if (strat2Name.isDefinedAt(strategy)) {
      ((), cs)
    } else {
      ((),
        (ts.declareStrategy(name) { strategy }(isTransition),
          strat2Name + (strategy -> name),
          superCluster2Strategies,
          place2Position))
    }
  })

  def addStrategyToClusteringMapAtPosition(tranStrategy: NonVariableStrategy, sClusterIndex: Int, clusterIndex: Int) = State((cs: CalculationState) => {
    val (ts, strat2Name, superCluster2Strategies, place2Position) = cs
    ((),
      (ts,
        strat2Name,
        superCluster2Strategies.updated(sClusterIndex,
          superCluster2Strategies.getOrElse(sClusterIndex, Map()).updated(
            clusterIndex, superCluster2Strategies.getOrElse(sClusterIndex, Map()).getOrElse(clusterIndex, Set()) + strat2Name(tranStrategy))),
          place2Position))
  })

  def createTransitionSpanningSeveralSuperClusters(transition: Transition): State[CalculationState, Unit] =
    for (
      cs <- State.get((cs: CalculationState) => cs);
      (ts, strat2Name, superCluster2Strategies, place2Position) = cs;
      placesGroupedBySuperClusters = (transition.inputArcs ++ transition.outputArcs).groupBy(a => place2Position(a.place)._1);
      placesGroupedByClusters = (transition.inputArcs ++ transition.outputArcs).groupBy(a => place2Position(a.place)._2);
      res <- calculateTransitionStrategy(transition, placesGroupedBySuperClusters, placesGroupedByClusters);
      (tranStrategy, isTransition) = res;
      _ <- saveStrategyWithName(transition.id, tranStrategy, isTransition);
      _ <- addStrategyToClusteringMapAtPosition(tranStrategy, placesGroupedBySuperClusters.keys.head, 
          if (placesGroupedByClusters.keys.size == 1)  placesGroupedByClusters.keys.head else -1) if (!isTransition)
    ) yield ()

  def calculateTransitionStrategy(transition: Transition, placesGroupedBySuperClusters: Map[Int, Set[Arc]], placesGroupedByClusters: Map[Int, Set[Arc]]): State[CalculationState, (NonVariableStrategy, Boolean)] =
    if (placesGroupedBySuperClusters.keys.size == 1) {
      if (placesGroupedByClusters.keys.size == 1) {
        for {
          cs <- State.get((cs: CalculationState) => cs)
          (ts, strat2Name, superCluster2Strategies, place2Position) = cs
          inputStrategies <- buildSequenceOfDependentStrategiesMonadic(transition.inputArcs.toList.sortBy(a => place2Position(a.place)._3).map(a => arcStrategy(a, inputArcStrategy _)))
          ouputStrategies <- buildSequenceOfDependentStrategiesMonadic(transition.outputArcs.toList.sortBy(a => place2Position(a.place)._3).map(a => arcStrategy(a, outputArcStrategy _)))
        } yield {
          logger.trace(s"Transition ${transition.id} is in completely in superCluster ${placesGroupedBySuperClusters.keys.head} and cluster ${placesGroupedByClusters.keys.head}")
          (Sequence(inputStrategies, ouputStrategies), false)
        }
      } else if (placesGroupedByClusters.keys.size > 1) { // more than one
        println(s"Creating transition rel for ${transition.id}")
        for {
          cs <- State.get((cs: CalculationState) => cs)
          (ts, strat2Name, superCluster2Strategies, place2Position) = cs
          inputStrategies <- buildSequenceOfDependentStrategiesMonadic(createListOfInputStrategiesForClusters(transition.inputArcs, place2Position))
          ouputStrategies <- buildSequenceOfDependentStrategiesMonadic(createListOfOuputStrategiesForClusters(transition.outputArcs, place2Position))
        } yield (Sequence(inputStrategies, ouputStrategies), false)
      } else {
        throw new IllegalStateException("Clusters must have at least one elt.")
      }
    } else { // multi super clustered transition
      //      println(s"Creating transition for multi-super-clustered transition ${transition.id}")
      for {
        cs <- State.get((cs: CalculationState) => cs)
        (ts, strat2Name, superCluster2Strategies, place2Position) = cs
        inputStrategies <- buildSequenceOfDependentStrategiesMonadic(createListOfInputStrategiesForSuperClusters(transition.inputArcs, place2Position))
        ouputStrategies <- buildSequenceOfDependentStrategiesMonadic(createListOfOutputStrategiesForSuperClusters(transition.outputArcs, place2Position))
      } yield (Sequence(inputStrategies, ouputStrategies), true)
    }

  def createListOfInputStrategiesForSuperClusters(inputArcs: Set[Arc], place2Position: Map[Place, (Int, Int, Int)]): List[State[CalculationState, NonVariableStrategy]] = {
    val arcsGroupedBySCluster = inputArcs.groupBy(a => place2Position(a.place)._1)
    //    println(arcsGroupedBySCluster)
    for (clusterIndex <- arcsGroupedBySCluster.keySet.toList.sortWith(_ < _)) yield {
      for (strategyForClusters <- buildSequenceOfDependentStrategiesMonadic(createListOfInputStrategiesForClusters(arcsGroupedBySCluster(clusterIndex), place2Position))) yield {
        ApplyToSCluster(strategyForClusters, clusterIndex): NonVariableStrategy
      }
    }
  }

  def createListOfOutputStrategiesForSuperClusters(outputArcs: Set[Arc], place2Position: Map[Place, (Int, Int, Int)]): List[State[CalculationState, NonVariableStrategy]] = {
    val arcsGroupedBySCluster = outputArcs.groupBy(a => place2Position(a.place)._1)
    for (clusterIndex <- arcsGroupedBySCluster.keySet.toList.sortWith(_ < _)) yield {
      for (strategyForClusters <- buildSequenceOfDependentStrategiesMonadic(createListOfOuputStrategiesForClusters(arcsGroupedBySCluster(clusterIndex), place2Position))) yield {
        ApplyToSCluster(strategyForClusters, clusterIndex): NonVariableStrategy
      }
    }
  }

  def createListOfInputStrategiesForClusters(inputArcs: Set[Arc], place2Position: Map[Place, (Int, Int, Int)]): List[State[CalculationState, NonVariableStrategy]] = {
    val arcsGroupedByCluster = inputArcs.toList.groupBy(a => place2Position(a.place)._2)
    for (clusterIndex <- arcsGroupedByCluster.keySet.toList.sortWith(_ < _)) yield {
      for (strategyForArcs <- buildSequenceOfDependentStrategiesMonadic(arcsGroupedByCluster(clusterIndex).toList.sortBy(a => place2Position(a.place)._3).map(a => arcStrategy(a, inputArcStrategy _)))) yield ApplyToCluster(strategyForArcs, clusterIndex): NonVariableStrategy
    }

  }

  def createListOfOuputStrategiesForClusters(outputArcs: Set[Arc], place2Position: Map[Place, (Int, Int, Int)]): List[State[CalculationState, NonVariableStrategy]] = {
    val arcsGroupedByCluster = outputArcs.toList.groupBy(a => place2Position(a.place)._2)
    for (clusterIndex <- arcsGroupedByCluster.keySet.toList.sortWith(_ < _)) yield {
      for (strategyForArcs <- buildSequenceOfDependentStrategiesMonadic(arcsGroupedByCluster(clusterIndex).toList.sortBy(a => place2Position(a.place)._3).map(a => arcStrategy(a, outputArcStrategy _)))) yield ApplyToCluster(strategyForArcs, clusterIndex): NonVariableStrategy
    }
  }

  def arcStrategy(arc: Arc, arcStrategyB: (Arc => State[CalculationState, NonVariableStrategy])): State[CalculationState, NonVariableStrategy] = {
    for (
      strategyBody <- arcStrategyB(arc);
      res <- State((cs: CalculationState) => {
        val (ts, strat2Name, superCluster2Strategies, place2Position) = cs
        strat2Name.lift(strategyBody) match {
          case None =>
            (DeclaredStrategyInstance(arc.id), (ts.declareStrategy(arc.id) { strategyBody }(false), strat2Name + (strategyBody -> arc.id), superCluster2Strategies, place2Position))
          case Some(stratName) =>
            (DeclaredStrategyInstance(stratName), (ts, strat2Name, superCluster2Strategies, place2Position))
        }
      })
    ) yield res
  }

  /**
   * Takes a list of petri net modules and transforms it to a transition system.
   * @param modules set of modules
   * @param net the original petri net that was transformed into modules.
   * @return a transition system for stratagem that calculates the state space of in input net.
   */
  def apply(modules: List[SuperCluster], net: PetriNet): TransitionSystem = {
    val initialModuleNumber = 0 // must be zero because of the indexes
    // creates a map that maps each place to its cluster p1 -> c1, p2 -> c1, pn -> cm, etc
    // we obtain a mapping from places to their super cluster, cluster and position
    val placeToSClusterClusterPosition = Map(modules.zipWithIndex.flatMap(e1 => e1._1.zipWithIndex.flatMap(e2 => e2._1.zipWithIndex.map(p => (p._1, (e1._2, e2._2, p._2))))).toArray: _*)
    val signWithSuperClusters = createSignatureSuperClusters(modules, basicSignature)
    // find max number of clusters per supercluster
    val maxClusters = modules.map(_.size).max
    println(maxClusters)
    // we create a function here
    val createSignWithClusters = ((for (i <- 0 to (maxClusters - 1)) yield (s: Signature) => s.withGenerator(s"c$i", CLUSTER_SORT_NAME, PLACE_SORT_NAME, CLUSTER_SORT_NAME)).reduce(_ compose _))
    val maxPlaces = (for (cluster <- modules; places <- cluster) yield places.size).max
    // here we also create a function
    val createSignWithPlaces = ((for (i <- 0 to (maxPlaces - 1)) yield (s: Signature) => s.withGenerator(s"p$i", PLACE_SORT_NAME, NAT_SORT_NAME, PLACE_SORT_NAME)).reduce(_ compose _))
    implicit val adt = new ADT(net.name, createSignWithClusters(createSignWithPlaces(signWithSuperClusters)))
      .declareVariable("p", PLACE_SORT_NAME)
      .declareVariable("x", NAT_SORT_NAME)
      .declareVariable("c", CLUSTER_SORT_NAME)
      .declareVariable("sc", SUPER_CLUSTER_SORT_NAME)

    val initialTransitionSystem = new TransitionSystem(adt, createInitialState(modules)(adt.term(END_SUPERCLUSTER)))
      .declareStrategy("applyOnce", S)(Choice(S, One(ApplyOnce(S), 2)))(false)
      .declareStrategy("applyOnceAndThen", S, Q)(IfThenElse(S, One(Q, 2), One(ApplyOnceAndThen(S, Q), 2)))(false)
    // add apply to scluster

    val transSystemState = for (
      _ <- addApplyToSCluster(modules.size);
      _ <- addApplyToCluster(maxClusters)
    ) yield ()

    val computationInitialState = (
      transSystemState.eval(initialTransitionSystem)._1,
      Map[NonVariableStrategy, String](),
      Map[Int, Map[Int, Set[String]]](),
      placeToSClusterClusterPosition)

    (for (
      _ <- calculateTransitionSystem(net.transitions);
      _ <- calculateSClusterSaturationStrategies;
      _ <- createAddSaturatingTransitions
    ) yield ()).eval(computationInitialState)._1._1
  }

  def createAddSaturatingTransitions: State[CalculationState, Unit] = State(cs => {
    val (ts, strat2Name, superCluster2LocalStrategies, place2Position) = cs
    val res = (for (superClusterIndex <- superCluster2LocalStrategies.keys.toList.sortWith(_ < _)) // for each local supercluster we need to create a fixpoint strategy
      yield addSaturatingTransitionForIndex(superClusterIndex)).reduceLeft((s1, s2) => s1.flatMap(_ => s2))
    ((), res.eval(cs)._1)
  })

  def addSaturatingTransitionForIndex(superClusterIndex: Int): State[CalculationState, Unit] = State(cs => {
    val (ts, strat2Name, superCluster2LocalStrategies, place2Position) = cs
    val res = (for (strategyName <- superCluster2LocalStrategies(superClusterIndex)(-1)) yield DeclaredStrategyInstance(strategyName): NonVariableStrategy).reduce((a,b) => Union(Try(a), Try(b)))
    ((),
      ({
        ts.declareStrategy(s"superClusterFixpoint$superClusterIndex") { ApplyToSCluster(FixPointStrategy(Union(res, Identity)), superClusterIndex) }(true)
      },
        strat2Name,
        superCluster2LocalStrategies,
        place2Position))
  })

  def calculateSClusterSaturationStrategies: State[CalculationState, Unit] = State(cs => {
    val (ts, strat2Name, superCluster2LocalStrategies, place2Position) = cs
    val res = (for (superClusterIndex <- superCluster2LocalStrategies.keys.toList.sortWith(_ < _)) // for each local supercluster we need to create a fixpoint strategy
      yield calculateSClusterSaturationStrategyForIndex(superClusterIndex)).reduceLeft((s1, s2) => s1.flatMap(_ => s2))
    ((), res.eval(cs)._1)
  })

  def calculateSClusterSaturationStrategyForIndex(i: Int): State[CalculationState, Unit] = State(cs => {
    val (ts, strat2Name, superCluster2LocalStrategies, place2Position) = cs
    val res = (for (clusterIndex <- superCluster2LocalStrategies(i).keys.toList.sortWith(_ < _)) // for each cluster we need we create a fix point
    yield {
      if (clusterIndex == -1) // no change that supercluster already has all transitions for that supercluster where they need to be 
        State((cs: CalculationState) => ((), cs))
      else // we need to add transitions that operate in that super cluster
        calculateClusterSaturationStrategyIndexForSingleCluster(i, clusterIndex) //we have a transition spanning one cluster in one super cluster, great!
    })
      .reduceLeft((s1, s2) => s1.flatMap(_ => s2))
    ((), res.eval(cs)._1)
  })

  def calculateClusterSaturationStrategyIndexForSingleCluster(superClusterIndex: Int, clusterIndex: Int): State[CalculationState, Unit] = State(cs => {
    val (ts, strat2Name, superCluster2LocalStrategies, place2Position) = cs
    val strategyBody = superCluster2LocalStrategies(superClusterIndex)(clusterIndex).map(DeclaredStrategyInstance(_): NonVariableStrategy).reduce((a,b) => Union(Try(a), Try(b)))
    val strategyName = "fixPoint_" + superClusterIndex + "_" + clusterIndex
    val newTS = ts.declareStrategy(strategyName) { ApplyToCluster(FixPointStrategy(Union(Identity, strategyBody)), clusterIndex) }(false)
    ((), (newTS,
      strat2Name,
      superCluster2LocalStrategies.updated(
        superClusterIndex, superCluster2LocalStrategies(superClusterIndex).updated(
          -1, superCluster2LocalStrategies(superClusterIndex).getOrElse(-1, Set()) + strategyName)),
        place2Position))
  })

  def calculateTransitionSystem(transitions: Set[Transition]): State[CalculationState, Unit] =
    transitions.map(t => transitionStrategy(t)).reduceLeft((s1, s2) => s1.flatMap(_ => s2))

  def createInitialStatePlaces(places: Cluster)(endTerm: ATerm)(implicit adt: ADT) =
    ((for (placesWithIndex <- places.zipWithIndex) yield (t: ATerm) => adt.term(s"p${placesWithIndex._2}", PredefADT.define(placesWithIndex._1.initialMarking, adt.term(PredefADT.ZERO), adt), t)).reduceLeft(_ compose _))(endTerm)

  def createInitialStateClusters(clusters: List[Cluster])(endTerm: ATerm)(implicit adt: ADT) =
    ((for (clustersWithIndex <- clusters.zipWithIndex) yield (t: ATerm) => adt.term(s"c${clustersWithIndex._2}", createInitialStatePlaces(clustersWithIndex._1)(adt.term(PetriNetADT.ENDPLACE)), t)).reduceLeft(_ compose _))(endTerm)

  def createInitialState(modules: List[SuperCluster])(endTerm: ATerm)(implicit adt: ADT) =
    ((for (clustersWithIndex <- modules.zipWithIndex) yield (t: ATerm) => adt.term(s"sc${clustersWithIndex._2}", createInitialStateClusters(clustersWithIndex._1)(adt.term(ENDCLUSTER)), t)).reduceLeft(_ compose _))(endTerm)

  implicit def equation2SimpleStrategy(eq: Equation) = SimpleStrategy(List(eq))

  def checkForSCluster(n: Int): State[TransitionSystem, NonVariableStrategy] =
    State(ts => (ts.adt.term(s"sc$n", ts.adt.term("c"), ts.adt.term("sc")) -> ts.adt.term(s"sc$n", ts.adt.term("c"), ts.adt.term("sc")), ts))

  def checkForCluster(n: Int): State[TransitionSystem, NonVariableStrategy] =
    State(ts => (ts.adt.term(s"c$n", ts.adt.term("p"), ts.adt.term("c")) -> ts.adt.term(s"c$n", ts.adt.term("p"), ts.adt.term("c")), ts))

  private def addApplyToSCluster(maxSCluster: Int): State[TransitionSystem, Unit] = {
    val r = for (i <- (0 to (maxSCluster - 1))) yield {
      // list of State containing the checkForCluster functions to check if we are bigger
      val checkNotBiggerThanClusterList = ((for (j <- (i + 1) to (maxSCluster - 1)) yield checkForSCluster(j)))
      // reduces the functions to a list of choices
      val checkNotBiggerThanCluster: State[TransitionSystem, NonVariableStrategy] =
        if (checkNotBiggerThanClusterList.isEmpty) State.insert(Fail) else checkNotBiggerThanClusterList.reduceLeft((state1, state2) => for (s1 <- state1; s2 <- state1) yield Choice(s1, s2))
      val checkSCluster = for (
        checkForiStrat <- checkForSCluster(i);
        checkNotBiggerThanClusterStrat <- checkNotBiggerThanCluster
      ) yield {
        IfThenElse(
          checkForiStrat,
          One(S, 1), // if we are in the right cluster, apply the strategy
          IfThenElse(checkNotBiggerThanClusterStrat,
            Fail,
            One(DeclaredStrategyInstance(s"applyForSCluster$i", S), 2))) // else we enter the recursion only if we are not bigger than the cluster
      }
      for (
        checkSClusterBody <- checkSCluster;
        res <- State((ts: TransitionSystem) => ((), ts.declareStrategy(s"applyForSCluster$i", S) { checkSClusterBody }(false)))
      ) yield res
    }
    r.reduceLeft((state1, state2) => state1.flatMap(_ => state2))
  }

  private def addApplyToCluster(maxCluster: Int): State[TransitionSystem, Unit] = {
    val r = for (i <- (0 to (maxCluster - 1))) yield {
      // list of State containing the checkForCluster functions to check if we are bigger
      val checkNotBiggerThanClusterList = ((for (j <- (i + 1) to (maxCluster - 1)) yield checkForCluster(j)))
      // reduces the functions to a list of choices
      val checkNotBiggerThanCluster: State[TransitionSystem, NonVariableStrategy] =
        if (checkNotBiggerThanClusterList.isEmpty) State.insert(Fail) else checkNotBiggerThanClusterList.reduceLeft((state1, state2) => for (s1 <- state1; s2 <- state1) yield Choice(s1, s2))
      val checkSCluster = for (
        checkForiStrat <- checkForCluster(i);
        checkNotBiggerThanClusterStrat <- checkNotBiggerThanCluster
      ) yield {
        IfThenElse(
          checkForiStrat,
          One(S, 1), // if we are in the right cluster, apply the strategy
          IfThenElse(checkNotBiggerThanClusterStrat,
            Fail,
            One(DeclaredStrategyInstance(s"applyForCluster$i", S), 2))) // else we enter the recursion only if we are not bigger than the cluster
      }
      for (
        checkClusterBody <- checkSCluster;
        res <- State((ts: TransitionSystem) => ((), ts.declareStrategy(s"applyForCluster$i", S) { checkClusterBody }(false)))
      ) yield res
    }
    r.reduceLeft((state1, state2) => state1.flatMap(_ => state2))
  }

}
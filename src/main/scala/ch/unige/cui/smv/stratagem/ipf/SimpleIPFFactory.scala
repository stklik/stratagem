package ch.unige.cui.smv.stratagem.ipf

import scala.collection.immutable.HashMap

import ch.unige.cui.smv.stratagem.util.StringSetWrapperFactory

class SimpleIPFFactory extends IPFAbstractFactory {

  type CanonicalType = SimpleIPFImplementation

  type FromType = Map[StringSetWrapperFactory.StringSetWrapper, StringSetWrapperFactory.StringSetWrapper]

  protected def makeFrom(alpha: AnyRef): SimpleIPFImplementation = alpha match {
    case a: HashMap[StringSetWrapperFactory.StringSetWrapper, StringSetWrapperFactory.StringSetWrapper] @unchecked => new SimpleIPFImplementation(a) 
    case _ => throw new IllegalArgumentException("Unable to create IPF")
  }

  def create(elt: String, image: String): SimpleIPFImplementation = create(
    HashMap(StringSetWrapperFactory.create(Set(elt)) -> StringSetWrapperFactory.create(Set(image))))

  /**
   * Tgus class is a simple implementation of the IPF. It maps sets of strings
   * to sets of strings.
   */
  class SimpleIPFImplementation protected[ipf] (
    val alpha: Map[StringSetWrapperFactory.StringSetWrapper, StringSetWrapperFactory.StringSetWrapper]) extends IPF {

    type LatticeElementType = SimpleIPFImplementation
    type DomainType = StringSetWrapperFactory.StringSetWrapper
    type ImageType = StringSetWrapperFactory.StringSetWrapper

    type DomainTypeElt = String
    type ImageTypeElt = String

    override val hashCode = alpha ##

    def asBinaryRelation: Set[(DomainTypeElt, ImageTypeElt)] = for (
      (key, value) <- alpha.toSet;
      elt1 <- key.set;
      elt2 <- value.set
    ) yield (elt1, elt2)

    override def equals(obj: Any) = obj match {
      case o: SimpleIPFImplementation => (o eq this) || this.alpha == o.alpha
      case _ => false
    }

    def v(that: SimpleIPFImplementation): SimpleIPFImplementation = create(alphaUnion(this.alpha, that.alpha))

    def ^(that: SimpleIPFImplementation): SimpleIPFImplementation = create(alphaIntersection(this.alpha, that.alpha))

    def \(that: SimpleIPFImplementation): SimpleIPFImplementation = create(alphaDifference(this.alpha, that.alpha))

    def bottomElement = create(Map.empty)
  }

} // end of InductiveIPFFactory
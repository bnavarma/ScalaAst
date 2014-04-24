package localhost

import scala.tools.nsc
import nsc.Global
import nsc.Phase
import nsc.plugins.Plugin
import nsc.plugins.PluginComponent
import nsc.ast.NodePrinters
import scala.tools.nsc.transform.Transform
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.HashSet
import scala.collection.mutable.HashMap
import scala.collection.mutable.ListBuffer
import scala.reflect.runtime.{universe => ru}
import scala.tools.reflect.ToolBox

class Avinash(val global: Global) extends Plugin {
  import global._
  import global.definitions._
  import global.analyzer._

  val name = "Avinash"
  val description = "Double Infinity"
  val components = List[PluginComponent](Component, Test)

  private object Component extends PluginComponent with Transform{
    val global: Avinash.this.global.type = Avinash.this.global
    val runsAfter = List[String]("parser");
    val phaseName = Avinash.this.name
    
	def newTransformer(unit: CompilationUnit) = new TemplateTransformer(unit)
	
	class TemplateTransformer(unit: CompilationUnit) extends Transformer {
		def preTransform(tree: Tree): Tree = 
		{
			var retTree = tree
			tree match{
				case vd @ ValDef(mods, name, tpt, rhs) =>
					for(c <- rhs.children)
					{
						println(c.getClass.getName+"\t"+c);
						c match {
							case a @ Select(qual, name) =>
								qual match{
									case n @ New(tpt) =>
								}
							case _ =>
						}
					}
					val newRhs = Apply(Select(New(Ident("pra")),nme.CONSTRUCTOR),List());
					retTree = treeCopy.ValDef(vd, mods, name, tpt, newRhs);
				case _ =>
			}
			retTree
		}
		def postTransform(tree: Tree): Tree = {
		tree match {
			case New(tpt) =>
				println("post-transforming new "+tpt )
				tree
			case _ => tree
		}}

		override def transform(tree: Tree): Tree = {
			postTransform(super.transform(preTransform(tree)));
		}
	}
  }

  private object Test extends PluginComponent{
    val global: Avinash.this.global.type = Avinash.this.global
    val runsAfter = List[String]("namer");
    val phaseName = "Traverser"
    
	def newPhase(_prev: Phase) = new AvinashPhase(_prev)    
	def newTraverser(): Traverser = new ForeachTreeTraverser(check)
	
	class AvinashPhase(prev: Phase) extends StdPhase(prev){
		def apply(unit: CompilationUnit){
			newTraverser().traverse(unit.body);
		}
	} 

	def check(tree: Tree): Unit = tree match {
		case vd @ ValDef(mods, name, tpt, rhs) => println("OMG");
		case _ =>
	}
  }
}
class pra{}
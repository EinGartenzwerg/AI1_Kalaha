package info.kwarc.teaching.AI.Kalah

import info.kwarc.teaching.AI.Kalah.Agents.{ HumanPlayer, RandomPlayer }
import info.kwarc.teaching.AI.Kalah.Interfaces.{ Fancy, Terminal }
import info.kwarc.teaching.AI.Kalah.util._
import info.kwarc.teaching.AI.Kalah.Agents.Chiron

// import WS1617.agents._

object Test {
  def main(args: Array[String]): Unit = {

    val int = new Fancy.FancyInterface(12)

    //new Game(new HumanPlayer("Hans"),new RandomPlayer("Hurtz"),int + Terminal)(12,12).play
    //new Game(new HumanPlayer("Hans"),new RandomPlayer("Hurtz"),int + Terminal)(12,12).play
    //new Game(new HumanPlayer("Hans"), new Chiron(), int + Terminal)(6, 6).play
//    new Game(new RandomPlayer("Hans"), new Chiron(), int + Terminal)(6, 6).play
//    new Game(new Chiron(),new RandomPlayer("Hans"), int + Terminal)(6, 6).play
    //new Game(new Chiron(),new HumanPlayer("Hans"), int + Terminal)(6, 6).play
//    new Game(new RandomPlayer("Hans"), new Chiron(), int + Terminal)(12, 12).play
    
    new Game( new Chiron(),new RandomPlayer("Hans"), int + Terminal)(12, 12).play
  }
}

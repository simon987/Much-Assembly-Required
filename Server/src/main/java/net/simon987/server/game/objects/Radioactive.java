package net.simon987.server.game.objects;

//Alpha: ±5cm
//Beta: 10-20 feet
//Gamma: 100+ feet

public interface Radioactive {

  public static int getAlphaCounts(double distance) {
    return (int) (1000 * 1.0 / (distance * distance));
  }
}

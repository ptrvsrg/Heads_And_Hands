package game;

import creature.*;
import creature.exception.*;
import random.RandomUtility;
import java.util.Scanner;

public class Game
{
    private Creature createCreature(FactoryMethod factory)
    {
        int attack = RandomUtility.randomInt(1, Creature.MAX_ATTACK);
        int defense = RandomUtility.randomInt(1, Creature.MAX_DEFENSE);
        int maxHealth = RandomUtility.randomInt(1, 100);
        int minDamage = RandomUtility.randomInt(1, 20);
        int maxDamage = RandomUtility.randomInt(minDamage, 20);

        try
        {
           return factory.create(attack,
                                     defense,
                                     maxHealth,
                                     minDamage,
                                     maxDamage);
        }
        catch(MaxHealthParamException |
              MaxDamageParamException |
              MinDamageParamException |
              AttackParamException |
              DefenseParamException ex)
        {
           throw new RuntimeException(ex);
        }
    }

    private void printCreatureProperty(Creature creature)
    {
        System.out.println("\tAttack: " + creature.getAttack());
        System.out.println("\tDefense: " + creature.getDefense());
        System.out.println("\tMax health: " + creature.getMaxHealth());
        System.out.println("\tDamage: " + creature.getMinDamage() + "-" + creature.getMaxDamage());
    }

    private void printHelp()
    {
        System.out.println("Available command:");
        System.out.println("\tattack - player and monster exchange blows, monster hits first");
        System.out.println("\theal - player heals, but monster continues to attack");
    }

    public void launch()
    {
        System.out.println("Creating player...");
        PlayerFactory playerFactory = new PlayerFactory();
        Creature player = createCreature(playerFactory);

        System.out.println("Player characteristics:");
        printCreatureProperty(player);
        System.out.println();

        System.out.println("Creating monster...");
        MonsterFactory monsterFactory = new MonsterFactory();
        Creature monster = createCreature(monsterFactory);

        System.out.println("Monster characteristics:");
        printCreatureProperty(monster);
        System.out.println();

        System.out.println("Your battle begins...");
        processCommand(player,
                       monster);

        if (monster.isDead())
            System.out.println("PLAYER WON");
        else if (player.isDead())
            System.out.println("MONSTER WON");
    }

    private void processCommand(Creature player,
                                Creature monster)
    {
        Scanner scanner = new Scanner(System.in);
        do
        {
            String command = scanner.next();

            switch (command)
            {
                case "help" -> printHelp();
                case "attack" ->
                {
                    monster.attack(player);

                    try
                    {
                        player.attack(monster);
                    }
                    catch (AttackingDeadException ignored)
                    {
                    }

                    System.out.println("Player health: " + player.getCurrentHealth());
                    System.out.println("Monster health: " + monster.getCurrentHealth());
                }
                case "heal" ->
                {
                    try
                    {
                        player.heal();
                    }
                    catch (AllHealingsAreUsedException |
                           FullHealthException ex)
                    {
                        System.err.println(ex.getLocalizedMessage());
                        break;
                    }

                    monster.attack(player);
                    System.out.println("Player health: " + player.getCurrentHealth());
                    System.out.println("Monster health: " + monster.getCurrentHealth());
                }
                default ->
                {
                    System.err.println("Unrecognized command");
                    printHelp();
                }
            }
        }
        while (!player.isDead() && !monster.isDead());

        scanner.close();
    }
}

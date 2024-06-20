package org.example;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.text.DateFormatSymbols;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * A fun Discord bot that provides various interactive commands and features.
 * Handles commands like ask, roll, joke18, joke, cat, meme, fact, profile, birthday, setcolor, pet, and help.
 * Also includes random pet interactions and user profile management.
 *
 * @author Rin
 */
public class SleepyyBot extends ListenerAdapter {


    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static final OkHttpClient httpClient = new OkHttpClient();
    private final Storage storage = new Storage();
    private final JSONObject data = storage.getData();

    //Experience points, levels, and cool-downs
    private static final int FEED_EXP = 50;
    private static final int PLAY_EXP = 20;
    private static final int FEED_COOLDOWN_HOURS = 4;
    private static final int PLAY_COOLDOWN_MINUTES = 10;
    private static final int MAX_LEVEL = 50;


    // Special actions
    private static final int FETCH_EXP = 15;
    private static final int TRICKS_EXP = 20;
    private static final int GUARD_EXP = 30;
    private static final int DANCE_EXP = 40;
    private static final int PLAYDEAD_EXP = 50;
    private static final int HIGHFIVE_EXP = 60;
    private static final int SING_EXP = 70;
    private static final int EXPLORE_EXP = 80;
    private static final int ADVENTURE_EXP = 100;
    private static final int HERO_EXP = 120;

    //Special abilities cool-downs
    private static final int FETCH_COOLDOWN_MINUTES = 15;
    private static final int TRICKS_COOLDOWN_MINUTES = 20;
    private static final int GUARD_COOLDOWN_MINUTES = 30;
    private static final int DANCE_COOLDOWN_MINUTES = 25;
    private static final int PLAYDEAD_COOLDOWN_MINUTES = 30;
    private static final int HIGHFIVE_COOLDOWN_MINUTES = 35;
    private static final int SING_COOLDOWN_MINUTES = 40;
    private static final int EXPLORE_COOLDOWN_MINUTES = 50;
    private static final int ADVENTURE_COOLDOWN_MINUTES = 60;
    private static final int HERO_COOLDOWN_MINUTES = 90;


    private static JDA jda = null; //Global scope for JDA

    /**
     * The main method to initialize and start the SleepyyBot.
     * Sets up the bot token, activity, event listeners, commands, and scheduled tasks.
     *
     * @param args command line arguments
     * @throws LoginException if the bot fails to login
     */
    public static void main(String[] args) throws LoginException {
        String token = "token"; //Bot's token

        JDABuilder builder = JDABuilder.createDefault(token);
        builder.setActivity(Activity.watching("Two sleepy humans!"));

        SleepyyBot bot = new SleepyyBot();
        builder.addEventListeners(bot);

        jda = builder.build();

        scheduler.scheduleAtFixedRate(bot::handleRandomPetInteractions, 0, 5, TimeUnit.HOURS); //Random pet interactions run

        // Register commands
        jda.updateCommands().addCommands(
                Commands.slash("ask", "Choose one option from multiple choices")
                        .addOptions(
                                new OptionData(OptionType.STRING, "option1", "First option", true),
                                new OptionData(OptionType.STRING, "option2", "Second option", true),
                                new OptionData(OptionType.STRING, "option3", "Third option", false),
                                new OptionData(OptionType.STRING, "option4", "Fourth option", false)
                        ),
                Commands.slash("roll", "Roll a die"),
                Commands.slash("joke18", "Get a random dirty joke"),
                Commands.slash("joke", "Get a random joke"),
                Commands.slash("cat", "Get a random cat image"),
                Commands.slash("meme", "Get a random meme"),
                Commands.slash("fact", "Get a random fact"),
                Commands.slash("profile", "View your profile information")
                        .addOption(OptionType.USER, "user", "The user whose profile you want to view", false),
                Commands.slash("birthday", "Set your birthday")
                        .addOption(OptionType.STRING, "date", "Your birthday (MM-DD)", true),
                Commands.slash("setcolor", "Set your profile color")
                        .addOption(OptionType.STRING, "color", "Hex code of your favorite color (e.g., #FF5733)", true),
                Commands.slash("pet", "Interact with your virtual pet")
                        .addOption(OptionType.STRING, "action", "Action (e.g., adopt, feed, play, info, name, delete, etc.)", true)
                        .addOption(OptionType.STRING, "name", "Name your pet", false),
                Commands.slash("help", "Get a list of all commands and their descriptions")
        ).queue();


    }


    /**
     * This method is called when a user interacts with a slash command.
     * It handles various slash commands based on the command name.
     *
     * @param event the event triggered by a slash command interaction,
     *              containing information about the command and the context of the interaction.
     */
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        switch (event.getName()) {
            case "ask":
                handleAskCommand(event);
                break;
            case "roll":
                handleRollCommand(event);
                break;
            case "8ball":
                handle8BallCommand(event);
                break;
            case "joke18":
                handleJoke18Command(event);
                break;
            case "joke":
                handleJokeCommand(event);
                break;
            case "cat":
                handleCatCommand(event);
                break;
            case "meme":
                handleMemeCommand(event);
                break;
            case "fact":
                handleFactCommand(event);
            case "profile":
                handleProfileCommand(event);
                break;
            case "birthday":
                handleBirthdayCommand(event);
                break;
            case "setcolor":
                handleSetColorCommand(event);
                break;
            case "pet":
                handlePetCommand(event);
                break;
            case "help":
                handleHelpCommand(event);
                break;
            default:
                event.reply("Unknown command.").queue();
                break;
        }
    }

    /**
     * This method handles the /ask command.
     * The user can input their options, and it will pick one randomly.
     *
     * @param event the event triggered by the /ask command interaction,
     *              containing the options provided by the user and the context of the interaction.
     */
    private void handleAskCommand(SlashCommandInteractionEvent event) {
        List<String> options = event.getOptions().stream()
                .map(OptionMapping::getAsString)
                .collect(Collectors.toList());
        if (options.size() > 0) {
            Random random = new Random();
            String choice = options.get(random.nextInt(options.size()));
            event.reply("I choose: " + choice).queue();
        } else {
            event.reply("You need to provide options!").queue();
        }
    }

    /**
     * This method handles the /roll command.
     * It rolls a die with a fixed maximum of 6 sides and returns a random result.
     *
     * @param event the event triggered by the /roll command interaction,
     *              containing the context of the interaction.
     */
    private void handleRollCommand(SlashCommandInteractionEvent event) {
        int sides = 6; // Fixed number of sides on the die
        Random random = new Random();
        int result = random.nextInt(sides) + 1;
        event.reply("You rolled a " + result + " (1-" + sides + ")").queue();
    }


    /**
     * This method handles the /8ball command.
     * It returns a random response as if from a magic 8-ball.
     *
     * @param event the event triggered by the /8ball command interaction,
     *              containing the context of the interaction.
     */
    private void handle8BallCommand(SlashCommandInteractionEvent event) {
        String[] responses = {
                "It is certain.",
                "It is decidedly so.",
                "Without a doubt.",
                "Yes – definitely.",
                "You may rely on it.",
                "As I see it, yes.",
                "Most likely.",
                "Outlook good.",
                "Yes.",
                "Signs point to yes.",
                "Reply hazy, try again.",
                "Ask again later.",
                "Better not tell you now.",
                "Cannot predict now.",
                "Concentrate and ask again.",
                "Don't count on it.",
                "My reply is no.",
                "My sources say no.",
                "Outlook not so good.",
                "Very doubtful."
        };
        Random random = new Random();
        String response = responses[random.nextInt(responses.length)];
        event.reply(response).queue();
    }

    /**
     * This method handles the /joke18 command.
     * It returns a random dirty joke.
     *
     * @param event the event triggered by the /joke18 command interaction,
     *              containing the context of the interaction.
     */
    private void handleJoke18Command(SlashCommandInteractionEvent event) {
        String[] jokes = {
                "Why isn't there a pregnant Barbie doll? Ken came in another box.",
                "Why did the snowman suddenly smile? He could see the snowblower coming.",
                "Why did the weatherman’s cheeks turn pink? He saw the climate change.",
                "What did Nala say to Simba in bed? Move fasta (Mufasa).",
                "What did Cinderella say to Prince Charming? Want to see if it fits?",
                "How did Burger King get Dairy Queen pregnant? He forgot to wrap his Whopper.",
                "Which animal has the largest chest? A Z-bra.",
                "Is that a mirror in your pocket? Because I can see myself in your pants.",
                "How does a wiener go camping? In a Wiener-bago.",
                "My wife asked me to spoon in bed, but I'd rather fork.",
                "What's the difference between peanut butter and jam? Only one has nuts.",
                "What does the horny toad say? Rub it.",
                "What do you call a nanny with breast implants? A faux-pair.",
                "What does a hot dog use for protection? Condoments.",
                "What does a robot do after a one-night stand? He nuts and bolts.",
                "What is a long, wide thing that men carry? A tie.",
                "Who is Cogsworth's best friend? His candlestick.",
                "What do you call an Italian hooker? A pasta-tute.",
                "What did Pongo and Perdita say after they did the deed? That hit the spot.",
                "Are you a pie? Because I'd like a piece of you.",
                "How did the police catch the naked man breaking into Zales? They grabbed him by the jewels.",
                "What's the difference between a snowman and a snow woman? Snowballs.",
                "What did Winnie-the-Pooh say to his new love interest? Show me the honey.",
                "Want to hear a dirty joke? A white horse fell in a mud puddle. Want to hear a clean joke? The white horse took a bath.",
                "What gets wetter when things get steamy? Steamboats.",
                "What's 6 inches long and has 2 nuts at the end? An Almond Joy.",
                "Why did the male chicken wear underwear on its head? Because its pecker was on its face.",
                "Can I watch TV? Yes, but don't turn it on.",
                "Why did the ranch blush? He saw the salad dressing.",
                "What's hot, pink and wet? A pig in a hot tub.",
                "Why couldn't the lizard get a girlfriend? Because he had a reptile dysfunction.",
                "Why did the fish blush? It saw the ocean's bottom.",
                "Why are men like popcorn? They satisfy you, but only for a little while.",
                "What kind of bees produce milk? Boo-bees.",
                "What's the difference between 'Oooh!' and 'Aaah!'? About three inches.",
                "What holds your buns firmly and makes them look round and pretty? A hair tie.",
                "What is Peter Pan's favorite place to eat out? Wendy's.",
                "Why did the mermaid wear seashells? She outgrew her b-shells.",
                "Why is a one-night stand with a man like a snowstorm? You never know how long it'll last.",
                "A couple were snuggling and his wife said softly, Speaking of fantasies, how about the one of you ironing?",
                "What do you play with at night that also vibrates? A cell phone.",
                "Why does it take 100 million sperm to fertilize one egg? Because they won't stop to ask for directions.",
                "What's in a man's pants that you won't find in a girl's pants? Pockets.",
                "What does one boob say to the other boob? If we don't get support, people will think we're nuts.",
                "What did Cinderella do when she got to the ball? She gagged.",
                "What did Nala say to Simba? Hakuna my tatas.",
                "Why did the sperm cross the road? Because I put on the wrong sock this morning.",
                "A cow has 4, but a woman only has 2. What am I? Legs.",
                "Do you work at Dick's? Because you're sporting the goods.",
                "What's the difference between a woman's husband and her boyfriend? 60 minutes.",
                "Were your parents bakers? They should have been because you've got a nice set of buns.",
                "Is your car battery dead? Because I'd really like to jump you.",
                "Did you butt dial me? I swear your booty is calling me.",
                "Why did Popeye punch the Pope? He heard he went to Mount Olive.",
                "What's the difference between a microwave and a woman? A man will actually press and pull a microwave's buttons and knobs.",
                "Are you Little Caesars? Because I'm hot and I'm ready.",
                "Are you a Slytherin? I hope so, because I really want you to slither into my Chamber of Secrets.",
                "What's the difference between you and an egg? An egg gets laid.",
                "Are you a trampoline? Because I'd really like to bounce on you.",
                "Are you a sea lion? Because I can see you lion in my bed tonight.",
                "What did the hurricane say to the coconut tree? Hold on to your nuts.",
                "What did the toaster say to the slice of bread? I want you inside me.",
                "Are you a firefighter? Because you make me hot and leave me wet.",
                "Wanna know something about Pinocchio? His nose isn't the only piece of wood that grows.",
                "What do you call a horny cow? Beef jerky.",
                "Why did the squirrel swim on its back? To keep its nuts dry.",
                "I can be short or long and women usually demand my full attention. What am I? A conversation.",
                "Are you a light switch? Because you turn me on.",
                "What's two inches wide, six inches long and makes everyone go crazy? A $100 bill.",
                "Are you my homework? Because I'm not doing you when I definitely should be.",
                "What is furry and peeking out of your pajamas at night? A person's head.",
                "Are you a Rubix cube? Because the more I play with you, the harder you get.",
                "What's made of rubber, handed out at some schools, and exists to prevent mistakes? Erasers.",
                "Why did the pool table laugh? Its balls were tickled.",
                "What gets longer when pulled, works best when jerks and inserts into a slot? A seatbelt.",
                "I asked my wife if she ever fantasizes about me, and she said yes – about me taking out the trash, mowing the lawn, and doing the dishes.",
                "Did you hear about the guy who died of a Viagra overdose? They couldn't close his casket.",
                "What do you call an expert fisherman? A master baiter.",
                "Are you a blanket? Because I love it when you're on top of me.",
                "What did the elephant ask the naked man? How do you breathe out of that thing?",
                "I get bigger each time you blow me. What am I? A balloon.",
                "What's Moby Dick's dad's name? Papa Boner.",
                "What's green and smells like pork? Kermit's finger.",
                "What did Pinocchio's lover say to him? Lie to me!",
                "What goes up, lets out a load and then goes back down? An elevator.",
                "What does the receptionist at a sperm bank say to clients as they leave? Thanks for coming!",
                "Knock knock. Who's there? Dentist. Dentist who? I heard you have some cavities that need filling?",
                "Knock knock. Who's there? Do you want two CDs? Do you want two CDs who? Do you want two CDs nudes?",
                "Knock knock. Who's there? When, where. When, where, who? Tonight, my place, me and you.",
                "Knock knock. Who's there? Willy. Willy who? Willy want to see you naked.",
                "Knock knock. Who's there? Dewey. Dewey who? Dewey have a condom around?",
                "Knock knock. Who's there? Justin. Justin who? You're Justin time to see me strip down for you.",
                "Knock, knock. Who's there? Dozer. Dozer who? Dozer some great assets you got there.",
                "Knock, knock. Who's there? Jamaican. Jamaican who? Jamaican me horny.",
                "Knock knock. Who's there? Tara. Tara Who? Tara McClozoff.",
                "Knock, knock. Who's there? Anita! Anita who? Anita you right now!",
                "Knock, knock. Who's there? Orange. Orange who? Orange you glad this isn't actually a banana?",
                "Knock, knock. Who's there? Amanda squeeze. Amanda squeeze who? You want amanda squeeze you all night?",
                "Knock, knock. Who's there? Waiter. Waiter who? Just waiter I get my hands on you."

        };
        Random random = new Random();
        String joke = jokes[random.nextInt(jokes.length)];
        event.reply(joke).queue();
    }

    /**
     * This method handles the /joke command.
     * It fetches a random joke from an external API and returns it.
     *
     * @param event the event triggered by the /joke command interaction,
     *              containing the context of the interaction.
     */
    private void handleJokeCommand(SlashCommandInteractionEvent event) {
        String apiUrl = "https://official-joke-api.appspot.com/random_joke";

        Request request = new Request.Builder()
                .url(apiUrl)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                JSONObject jsonObject = new JSONObject(response.body().string());
                String setup = jsonObject.getString("setup");
                String punchline = jsonObject.getString("punchline");
                event.reply(setup + " " + punchline).queue();
            } else {
                event.reply("Couldn't fetch a joke at the moment. Try again later!").queue();
            }
        } catch (IOException e) {
            event.reply("An error occurred while fetching a joke.").queue();
            e.printStackTrace();
        }
    }

    /**
     * This method handles the /cat command.
     * It fetches a random cat image from an external API and returns it.
     *
     * @param event the event triggered by the /cat command interaction,
     *              containing the context of the interaction.
     */
    private void handleCatCommand(SlashCommandInteractionEvent event) {
        String apiUrl = "https://api.thecatapi.com/v1/images/search";

        Request request = new Request.Builder()
                .url(apiUrl)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                JSONArray jsonArray = new JSONArray(response.body().string());
                if (jsonArray.length() > 0) {
                    JSONObject catObject = jsonArray.getJSONObject(0);
                    String imageUrl = catObject.getString("url");
                    event.reply(imageUrl).queue();
                } else {
                    event.reply("Couldn't find a cat image at the moment. Try again later!").queue();
                }
            } else {
                event.reply("Failed to fetch a cat image. Try again later!").queue();
            }
        } catch (IOException e) {
            event.reply("An error occurred while fetching a cat image.").queue();
            e.printStackTrace();
        }
    }

    /**
     * This method handles the /meme command.
     * It fetches a random meme from an external API and returns it.
     *
     * @param event the event triggered by the /meme command interaction,
     *              containing the context of the interaction.
     */
    private void handleMemeCommand(SlashCommandInteractionEvent event) {
        String apiUrl = "https://meme-api.com/gimme";

        Request request = new Request.Builder()
                .url(apiUrl)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                String responseBody = response.body().string();
                JSONObject jsonObject = new JSONObject(responseBody);
                String memeUrl = jsonObject.getString("url");
                String title = jsonObject.getString("title");

                EmbedBuilder embedBuilder = new EmbedBuilder();
                embedBuilder.setTitle(title);
                embedBuilder.setImage(memeUrl);

                event.replyEmbeds(embedBuilder.build()).queue();
            } else {
                event.reply("Couldn't fetch a meme at the moment. Try again later!").queue();
            }
        } catch (IOException e) {
            event.reply("An error occurred while fetching a meme.").queue();
            e.printStackTrace();
        }
    }

    /**
     * This method handles the /fact command.
     * It fetches a random fact from an external API and returns it.
     *
     * @param event the event triggered by the /fact command interaction,
     *              containing the context of the interaction.
     */
    private void handleFactCommand(SlashCommandInteractionEvent event) {
        String apiUrl = "https://uselessfacts.jsph.pl/random.json?language=en";

        Request request = new Request.Builder()
                .url(apiUrl)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                JSONObject jsonObject = new JSONObject(response.body().string());
                String fact = jsonObject.getString("text");
                event.reply(fact).queue();
            } else {
                event.reply("Couldn't fetch a fact at the moment. Try again later!").queue();
            }
        } catch (IOException e) {
            event.reply("An error occurred while fetching a fact.").queue();
            e.printStackTrace();
        }
    }

    /**
     * This method handles the /profile command.
     * It fetches and displays the specified user's profile information.
     *
     * @param event the event triggered by the /profile command interaction,
     *              containing the context of the interaction.
     */
    private void handleProfileCommand(SlashCommandInteractionEvent event) {
        OptionMapping userOption = event.getOption("user");
        String userId = userOption != null ? userOption.getAsUser().getId() : event.getUser().getId();
        String username = userOption != null ? userOption.getAsUser().getName() : event.getUser().getName();
        String avatarUrl = userOption != null ? userOption.getAsUser().getEffectiveAvatarUrl() : event.getUser().getEffectiveAvatarUrl();

        String color = data.has(userId) ? data.getJSONObject(userId).optString("color", "#FFFFFF") : "#FFFFFF";
        String birthday = data.has(userId) ? data.getJSONObject(userId).optString("birthday", "Not set") : "Not set";
        String petName = data.has(userId) ? data.getJSONObject(userId).optString("petName", "No pet adopted") : "No pet adopted";
        String petLevel = data.has(userId) ? String.valueOf(data.getJSONObject(userId).optInt("petLevel", 0)) : "0";
        String petImage = getPetImage(data.has(userId) ? data.getJSONObject(userId).optInt("petLevel", 0) : 0);

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(username);
        embedBuilder.setThumbnail(avatarUrl);
        embedBuilder.setColor(java.awt.Color.decode(color));
        embedBuilder.setDescription("Here is the profile information.");
        embedBuilder.addField("Birthday", birthday, true);
        embedBuilder.addField("Pet Name", petName, true);
        embedBuilder.addField("Pet Level", petLevel, true);

        if (petImage != null) {
            embedBuilder.setImage(petImage);
        }

        event.replyEmbeds(embedBuilder.build()).queue();
    }

    /**
     * This method handles the /birthday command.
     * It allows the user to set their birthday.
     *
     * @param event the event triggered by the /birthday command interaction,
     *              containing the birthday date provided by the user and the context of the interaction.
     */
    private void handleBirthdayCommand(SlashCommandInteractionEvent event) {
        String userId = event.getUser().getId();
        String dateInput = event.getOption("date").getAsString();

        // Parse the input date
        String[] dateParts = dateInput.split("-");
        if (dateParts.length != 2) {
            event.reply("Invalid date format. Please use MM-DD format.").queue();
            return;
        }

        int monthNumber;
        int dayNumber;
        try {
            monthNumber = Integer.parseInt(dateParts[0]);
            dayNumber = Integer.parseInt(dateParts[1]);
        } catch (NumberFormatException e) {
            event.reply("Invalid date format. Please use MM-DD format.").queue();
            return;
        }

        if (monthNumber < 1 || monthNumber > 12 || dayNumber < 1 || dayNumber > 31) {
            event.reply("Invalid date. Please use a valid month (1-12) and day (1-31).").queue();
            return;
        }

        // Convert month number to month name
        String monthName = new DateFormatSymbols().getMonths()[monthNumber - 1];

        // Save the birthday in the desired format
        String formattedBirthday = monthName + " " + dayNumber;

        if (!data.has(userId)) {
            data.put(userId, new JSONObject());
        }

        data.getJSONObject(userId).put("birthday", formattedBirthday);
        storage.save();
        event.reply("Your birthday has been set to " + formattedBirthday + "!").queue();
    }

    /**
     * This method handles the /setcolor command.
     * It allows the user to set their profile color using a hex code.
     *
     * @param event the event triggered by the /setcolor command interaction,
     *              containing the hex code provided by the user and the context of the interaction.
     */
    private void handleSetColorCommand(SlashCommandInteractionEvent event) {
        String userId = event.getUser().getId();
        String color = event.getOption("color").getAsString();

        if (!color.matches("^#[0-9A-Fa-f]{6}$")) {
            event.reply("Invalid color format. Please use a hex code (e.g., #FF5733).").queue();
            return;
        }

        if (!data.has(userId)) {
            data.put(userId, new JSONObject());
        }

        data.getJSONObject(userId).put("color", color);
        storage.save();
        event.reply("Your profile color has been set to " + color + "!").queue();
    }

    /**
     * This method handles the /pet command.
     * It allows the user to interact with their virtual pet, perform various actions, and manage the pet.
     *
     * @param event the event triggered by the /pet command interaction,
     *              containing the action provided by the user and the context of the interaction.
     */
    private void handlePetCommand(SlashCommandInteractionEvent event) {
        String userId = event.getUser().getId();
        String action = event.getOption("action").getAsString().toLowerCase();
        OptionMapping nameOption = event.getOption("name");

        if (!data.has(userId)) {
            data.put(userId, new JSONObject());
        }

        JSONObject userData = data.getJSONObject(userId);

        switch (action) {
            case "adopt":
                if (userData.has("petName")) {
                    event.reply("You already have a pet named " + userData.getString("petName") + ". You can't adopt another one.").queue();
                    return;
                }
                if (nameOption == null) {
                    event.reply("Please provide a name for your pet.").queue();
                    return;
                }
                String petName = nameOption.getAsString();
                userData.put("petName", petName);
                userData.put("petLevel", 1);
                userData.put("petExp", 0);
                userData.put("lastFeedTime", 0);
                userData.put("lastPlayTime", 0);
                storage.save();
                event.reply("You have adopted a new pet named " + petName + "!").queue();
                break;
            case "feed":
                handlePetAction(event, userId, "feed");
                break;
            case "play":
                handlePetAction(event, userId, "play");
                break;
            case "name":
                if (nameOption == null) {
                    event.reply("Please provide a new name for your pet.").queue();
                    return;
                }
                String newName = nameOption.getAsString();
                userData.put("petName", newName);
                storage.save();
                event.reply("Your pet's name has been changed to " + newName + "!").queue();
                break;
            case "delete":
                deletePet(userId);
                event.reply("Your pet has been deleted.").queue();
                break;
            case "info":
                handlePetInfoCommand(event, userId);
                break;
            default:
                handleSpecialPetActions(event, userId, action);
                break;
        }

        storage.save();
    }

    /**
     * Deletes the pet data for a specified user.
     *
     * @param userId the ID of the user whose pet data is to be deleted
     */
    private void deletePet(String userId) {
        if (data.has(userId)) {
            JSONObject userData = data.getJSONObject(userId);
            userData.remove("petName");
            userData.remove("petLevel");
            userData.remove("petExp");
            userData.remove("lastFeedTime");
            userData.remove("lastPlayTime");
            userData.remove("petImage"); // Remove any pet image data if exists
            storage.save();
        }
    }

    /**
     * Handles the /pet info command.
     * Displays information about the user's pet.
     *
     * @param event  the event triggered by the /pet info command interaction
     * @param userId the ID of the user requesting pet info
     */
    private void handlePetInfoCommand(SlashCommandInteractionEvent event, String userId) {
        JSONObject userData = data.getJSONObject(userId);
        if (!userData.has("petName")) {
            event.reply("You don't have a pet yet. Use `/pet adopt` to get one.").queue();
            return;
        }

        String petName = userData.getString("petName");
        int petLevel = userData.getInt("petLevel");
        int petExp = userData.getInt("petExp");
        int requiredExp = getRequiredExperience(petLevel);
        String petImage = getPetImage(petLevel);

        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle(petName + " (Level " + petLevel + ")");
        embed.setDescription("Here is the information about your pet:");
        embed.setThumbnail(petImage); // URL of the pet image based on its level
        embed.setColor(0xFBAED2); // Lavender pink color

        // Adding pet details
        embed.addField("Experience", petExp + " / " + requiredExp, false);
        embed.addField("Abilities", getNewAbilities(petLevel), false);

        event.replyEmbeds(embed.build()).queue();
    }

    /**
     * Handles the specified pet action (feed or play) for a user.
     *
     * @param event  the event triggered by the /pet feed or /pet play command interaction
     * @param userId the ID of the user performing the action
     * @param action the action being performed (feed or play)
     */
    private void handlePetAction(SlashCommandInteractionEvent event, String userId, String action) {
        JSONObject userData = data.getJSONObject(userId);
        int petLevel = userData.optInt("petLevel", 1);
        int petExp = userData.optInt("petExp", 0);
        long lastFeedTime = userData.optLong("lastFeedTime", 0);
        long lastPlayTime = userData.optLong("lastPlayTime", 0);
        long currentTime = Instant.now().getEpochSecond();

        int expGain;
        long cooldownTime;
        String lastActionTimeField;

        switch (action) {
            case "feed":
                expGain = FEED_EXP;
                cooldownTime = FEED_COOLDOWN_HOURS * 3600;
                lastActionTimeField = "lastFeedTime";
                break;
            case "play":
                expGain = PLAY_EXP;
                cooldownTime = PLAY_COOLDOWN_MINUTES * 60;
                lastActionTimeField = "lastPlayTime";
                break;
            case "fetch":
                expGain = FETCH_EXP;
                cooldownTime = FETCH_COOLDOWN_MINUTES * 60;
                lastActionTimeField = "lastFetchTime";
                break;
            case "tricks":
                expGain = TRICKS_EXP;
                cooldownTime = TRICKS_COOLDOWN_MINUTES * 60;
                lastActionTimeField = "lastTricksTime";
                break;
            case "guard":
                expGain = GUARD_EXP;
                cooldownTime = GUARD_COOLDOWN_MINUTES * 60;
                lastActionTimeField = "lastGuardTime";
                break;
            case "dance":
                expGain = DANCE_EXP;
                cooldownTime = DANCE_COOLDOWN_MINUTES * 60;
                lastActionTimeField = "lastDanceTime";
                break;
            case "playdead":
                expGain = PLAYDEAD_EXP;
                cooldownTime = PLAYDEAD_COOLDOWN_MINUTES * 60;
                lastActionTimeField = "lastPlaydeadTime";
                break;
            case "highfive":
                expGain = HIGHFIVE_EXP;
                cooldownTime = HIGHFIVE_COOLDOWN_MINUTES * 60;
                lastActionTimeField = "lastHighfiveTime";
                break;
            case "sing":
                expGain = SING_EXP;
                cooldownTime = SING_COOLDOWN_MINUTES * 60;
                lastActionTimeField = "lastSingTime";
                break;
            case "explore":
                expGain = EXPLORE_EXP;
                cooldownTime = EXPLORE_COOLDOWN_MINUTES * 60;
                lastActionTimeField = "lastExploreTime";
                break;
            case "adventure":
                expGain = ADVENTURE_EXP;
                cooldownTime = ADVENTURE_COOLDOWN_MINUTES * 60;
                lastActionTimeField = "lastAdventureTime";
                break;
            case "hero":
                expGain = HERO_EXP;
                cooldownTime = HERO_COOLDOWN_MINUTES * 60;
                lastActionTimeField = "lastHeroTime";
                break;
            default:
                event.reply("Unknown action.").queue();
                return;
        }

        long lastActionTime = userData.optLong(lastActionTimeField, 0);
        if (currentTime - lastActionTime < cooldownTime) {
            long remainingCooldown = cooldownTime - (currentTime - lastActionTime);
            event.reply("You can perform this action again in " + remainingCooldown / 60 + " minutes and " + remainingCooldown % 60 + " seconds.").queue();
            return;
        }

        petExp += expGain;
        boolean leveledUp = false;
        while (petExp >= getRequiredExperience(petLevel) && petLevel < MAX_LEVEL) {
            petExp -= getRequiredExperience(petLevel);
            petLevel++;
            leveledUp = true;
        }

        userData.put("petExp", petExp);
        userData.put("petLevel", petLevel);
        userData.put(lastActionTimeField, currentTime);

        event.reply("You performed the " + action + " action with your pet!").queue();

        if (leveledUp) {
            String levelUpMessage = "Your pet leveled up to level " + petLevel + "! ";
            levelUpMessage += getNewAbilities(petLevel);
            userData.put("petImage", getPetImage(petLevel));
            event.getChannel().sendMessage(levelUpMessage).queue();
        }

        storage.save();
    }

    /**
     * Returns the required experience points for a given pet level.
     *
     * @param level the level of the pet
     * @return the required experience points for the given level
     */
    private int getRequiredExperience(int level) {
        return (int) Math.pow(100 * level, 1.5);
    }


    /**
     * Handles special pet actions (fetch, tricks, guard, dance, playdead, highfive, sing, explore, adventure, hero).
     *
     * @param event  the event triggered by the /pet special action command interaction
     * @param userId the ID of the user performing the special action
     * @param action the special action being performed
     */
    private void handleSpecialPetActions(SlashCommandInteractionEvent event, String userId, String action) {
        JSONObject userData = data.getJSONObject(userId);
        int petLevel = userData.optInt("petLevel", 1);

        switch (action) {
            case "fetch":
                if (petLevel >= 5) {
                    event.reply("Your pet fetched a ball and brought it back to you!").queue();
                } else {
                    event.reply("Your pet needs to be at least level 5 to fetch.").queue();
                }
                break;
            case "tricks":
                if (petLevel >= 10) {
                    event.reply("Your pet performed a trick!").queue();
                } else {
                    event.reply("Your pet needs to be at least level 10 to perform tricks.").queue();
                }
                break;
            case "guard":
                if (petLevel >= 15) {
                    event.reply("Your pet is guarding you!").queue();
                } else {
                    event.reply("Your pet needs to be at least level 15 to guard.").queue();
                }
                break;
            case "dance":
                if (petLevel >= 20) {
                    event.reply("Your pet danced around happily!").queue();
                } else {
                    event.reply("Your pet needs to be at least level 20 to dance.").queue();
                }
                break;
            case "playdead":
                if (petLevel >= 25) {
                    event.reply("Your pet played dead convincingly!").queue();
                } else {
                    event.reply("Your pet needs to be at least level 25 to play dead.").queue();
                }
                break;
            case "highfive":
                if (petLevel >= 30) {
                    event.reply("Your pet gave you a high five!").queue();
                } else {
                    event.reply("Your pet needs to be at least level 30 to give high fives.").queue();
                }
                break;
            case "sing":
                if (petLevel >= 35) {
                    event.reply("Your pet sang a cute song!").queue();
                } else {
                    event.reply("Your pet needs to be at least level 35 to sing.").queue();
                }
                break;
            case "explore":
                if (petLevel >= 40) {
                    event.reply("Your pet explored the surroundings and found something interesting!").queue();
                } else {
                    event.reply("Your pet needs to be at least level 40 to explore.").queue();
                }
                break;
            case "adventure":
                if (petLevel >= 45) {
                    event.reply("Your pet went on an adventure and had a great time!").queue();
                } else {
                    event.reply("Your pet needs to be at least level 45 to go on adventures.").queue();
                }
                break;
            case "hero":
                if (petLevel == 50) {
                    event.reply("Your pet performed a heroic act and saved the day!").queue();
                } else {
                    event.reply("Your pet needs to be level 50 to perform heroic acts.").queue();
                }
                break;
            default:
                event.reply("Unknown action. Please use a valid action.").queue();
                break;
        }
    }

    /**
     * Returns a message describing new abilities unlocked at a given pet level.
     *
     * @param level the level of the pet
     * @return a message describing the new abilities unlocked at the given level
     */
    private String getNewAbilities(int level) {
        switch (level) {
            case 5:
                return "Your pet can now fetch items!";
            case 10:
                return "Your pet can perform tricks!";
            case 15:
                return "Your pet can guard you!";
            case 20:
                return "Your pet can dance!";
            case 25:
                return "Your pet can play dead!";
            case 30:
                return "Your pet can give high fives!";
            case 35:
                return "Your pet can sing!";
            case 40:
                return "Your pet can explore!";
            case 45:
                return "Your pet can go on adventures!";
            case 50:
                return "Your pet can perform heroic acts!";
            default:
                return "Your pet is still growing. More abilities to come!";
        }
    }

    /**
     * Returns the URL of a pet image based on the pet's level.
     *
     * @param level the level of the pet
     * @return the URL of the pet image
     */
    private String getPetImage(int level) {
        // Return different image URLs based on the pet's level
        if (level >= 45) {
            return "https://i.pinimg.com/736x/d3/98/e4/d398e45d1df8198e61243613d446fdeb.jpg";
        } else if (level >= 30) {
            return "https://i.pinimg.com/564x/61/fd/c1/61fdc19fabf4204dfc279dc3e1eb1d56.jpg";
        } else if (level >= 15) {
            return "https://i.pinimg.com/564x/11/00/82/110082f7b552d9fe23fbde5942571722.jpg";
        } else if (level >= 10) {
            return "https://i.pinimg.com/564x/a9/39/5e/a9395e3e7abb065560726c00faf1c0c9.jpg";
        } else if (level >= 5) {
            return "https://i.pinimg.com/564x/84/8b/49/848b4980d6395354b5f6a09558c0cbdc.jpg";
        } else {
            return "https://i.pinimg.com/564x/df/bc/75/dfbc75c63b8be1a6192147a30e34a3d1.jpg";
        }
    }

    /**
     * Sends a message to a specific channel.
     *
     * @param jda     the JDA instance
     * @param channelId the ID of the channel to send the message to
     * @param message the message to send
     */
    private void sendMessageToChannel(JDA jda, String channelId, String message) {
        jda.getTextChannelById(channelId).sendMessage(message).queue();
    }

    /**
     * Handles random pet interactions between users with pets.
     * Pairs users randomly and generates interaction messages.
     */
    private void handleRandomPetInteractions() {
        // Check if enough time has passed since the last event
        long currentTime = Instant.now().getEpochSecond();
        long lastEventTime = storage.getLastEventTime();
        long eventInterval = 5 * 60 * 60; // 5 hours in seconds

        if (currentTime - lastEventTime < eventInterval) {
            return; // Not enough time has passed since the last event
        }

        // Collect all users with pets
        List<String> usersWithPets = new ArrayList<>();
        for (String userId : data.keySet()) {
            JSONObject userData = data.getJSONObject(userId);
            if (userData.has("petName")) {
                usersWithPets.add(userId);
            }
        }

        // Only proceed if there are 2 or more users with pets
        if (usersWithPets.size() < 2) {
            return;
        }

        // Shuffle the list to randomize pairings
        Collections.shuffle(usersWithPets);

        // Iterate through pairs and create interactions
        for (int i = 0; i < usersWithPets.size() - 1; i += 2) {
            String userId1 = usersWithPets.get(i);
            String userId2 = usersWithPets.get(i + 1);

            JSONObject userData1 = data.getJSONObject(userId1);
            JSONObject userData2 = data.getJSONObject(userId2);

            String petName1 = userData1.getString("petName");
            String petName2 = userData2.getString("petName");

            // Perform a random interaction
            Random random = new Random();
            int interactionType = random.nextInt(3); // 0, 1, or 2

            String interactionMessage;

            switch (interactionType) {
                case 0:
                    interactionMessage = petName1 + " and " + petName2 + " played together and had fun!";
                    break;
                case 1:
                    interactionMessage = petName1 + " and " + petName2 + " found a toy and played with it!";
                    break;
                case 2:
                    interactionMessage = petName1 + " and " + petName2 + " learned a new trick together!";
                    break;
                default:
                    interactionMessage = petName1 + " and " + petName2 + " are having a quiet time together.";
                    break;
            }

            // Send the interaction message to the general chat
            String generalChannelId = "YOUR_CHANNEL_ID"; // Replace with your general channel ID
            sendMessageToChannel(jda, generalChannelId, interactionMessage);
        }

        // Update the timestamp of the last event
        storage.setLastEventTime(currentTime);
        storage.save();
    }


    /**
     * This method handles the /help command.
     * It displays a list of all available commands and their descriptions.
     *
     * @param event the event triggered by the /help command interaction,
     *              containing the context of the interaction.
     */
    private void handleHelpCommand(SlashCommandInteractionEvent event) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Sleepyy Bot Help");
        embed.setDescription("Here is a list of all available commands:");
        embed.setThumbnail("https://i.pinimg.com/736x/fd/80/74/fd807475a25a7fc5847e4a7777b3a70c.jpg");
        embed.setColor(0xFBAED2); // Set to your desired color

        // Adding commands to the embed
        embed.addField("/ask", "Choose one option from multiple choices\nOptions: [option1, option2, option3 (optional), option4 (optional)]", false);
        embed.addField("/roll", "Roll a die and get a number", false);
        embed.addField("/joke18", "Get a random dirty joke", false);
        embed.addField("/joke", "Get a random joke", false);
        embed.addField("/cat", "Get a random cat image", false);
        embed.addField("/meme", "Get a random meme", false);
        embed.addField("/fact", "Get a random fact", false);
        embed.addField("/profile", "View your profile information", false);
        embed.addField("/birthday", "Set your birthday [MM-DD]", false);
        embed.addField("/setcolor", "Set your profile color\nOptions: [color (hex code, e.g., #FF5733)]", false);
        embed.addField("/pet", "Interact with your virtual pet\nOptions: [action (e.g., adopt, feed, play, name, info, fetch, tricks, guard, dance, playdead, highfive, sing, explore, adventure, hero, delete)], [name (for 'adopt' or 'name' action)]", false);
        embed.addField("/help", "Get a list of all commands and their descriptions", false);

        event.replyEmbeds(embed.build()).queue();
    }


}






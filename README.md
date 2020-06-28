# The REPL as a General Purpose Tool - Workshop
Clojure North 2020

Developed By: Wesley A Matson

----

## Participant Requirements

- Internet Connection
- REPL/Dependency Management: [clj tools.deps](https://clojure.org/guides/getting_started)
- IDE/Text Editor with REPL integration and Paren Management
  - Examples will be given in [VS Code](https://code.visualstudio.com/) with the [Calva](https://marketplace.visualstudio.com/items?itemName=betterthantomorrow.calva) extension
  - [IntelliJ](https://www.jetbrains.com/idea/) with the [Cursive](https://cursive-ide.com/) extension can provide a familiar experience for Java devs (free for non-commercial use)
  - [Emacs](https://www.gnu.org/software/emacs/) with the [CIDER](https://github.com/clojure-emacs/cider) package offers a unique/useful data explorer, a decent config can be found [here](https://github.com/wmatson/emacs-config) (not recommended for beginner Clojurists)
- Verify your setup by cloning this repo and starting your REPL/jacking in
  - Calva on Windows (jack-in): Ctrl + Alt + C, Ctrl + Alt J
  - Emacs/CIDER: C-c, M-j
  
## Outline

- Intro/Icebreaker Exercise (10 minutes)
  - Come with your favorite function (Clojure core preferred) in mind
- Data Manipulation Exercise (15 minutes)
- Data Joining Exercise (20 minutes)
- "Next Steps" (5 minutes)
- Q&A (10 minutes)

----

Now that the conference is over, I've taken down the relevant AWS resources, but you can run the full set on localhost with the following command in the root directory of this repo:

```
docker-compose up --build
```

This will take a few minutes the first time, but should be fairly quick thereafter.

If you take everything down and run this same command a second time, the mysql instance won't be rebuilt so the init script will fail, this doesn't hurt anything.

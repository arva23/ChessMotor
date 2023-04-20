# ChessMotor
Chessmotor is a Java based chess application with a graphical user interface.
Its initial purpose is to provide a machine side player step generator algorithm.
It also consists of a graphical user interface based I/O interface to be able to 
interact with the engine conveninently. It provides human-machine player interaction 
based gameplays. It uses a linearized tree data structure for machine player generation.

## Available functions
- Save game status (using GUI)
- Load game status (using GUI)
- Render visual chess board
- Accept piece actions from game board
- Pawn replacement in case of enemy baseline reach (promotion)
- Standard step generations for machine player
- Standard step validations for human and machine player
- Counting human and machine player time with timout evaluation
- Counting players' accumulated step scores (step strength)
- Evaluation of gameplay conditions (check, checkmate, give up)
- Start game at arbitrary time (not instantenously after start of
the application or load of previously saved game)

## Licence
[MIT](https://choosealicense.com/licenses/mit/)

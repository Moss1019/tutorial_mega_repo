#include <iostream>

#include <Box2D/box2d.h>

#include <SFML/Graphics.hpp>

constexpr float PIXELS_PER_METER = 50.0f;

constexpr unsigned WIDTH_IN_PIXELS = 800;
constexpr unsigned HEIGHT_IN_PIXELS = 600;

constexpr float PADDLE_SPEED = 5.0f;
constexpr float PADDLE_AI_SPEED = 0.25f;

// Conversions work because the game world is static, no screen movement
// Convert from box2d meters to sfml pixels on the screen
inline sf::Vector2f b2ToSfml(const b2Vec2 &v)
{
    return {v.x * PIXELS_PER_METER, v.y * PIXELS_PER_METER};
}

// Convert from sfml pixels to box2d meters within the simulated word
inline b2Vec2 sfmlToB2(const sf::Vector2f &v)
{
    return {v.x / PIXELS_PER_METER, v.y / PIXELS_PER_METER};
}

// Box2D -> Uses meters
// SFML -> pixels

// Physics entity
// 1. Body representation in out physics simulation
// 2. Shape config and geometry
// 3. graphic

int main()
{
    // Load the font at the start, this is a relatively expensive step
    sf::Font font;
    if (!font.openFromFile("assets/font.ttf"))
    {
        std::cout << "Failed to load font" << std::endl;
        return -1;
    }

    // SFML specific text objects
    // TEXT
    sf::Text scoreText(font, "Score: 0");
    scoreText.setFillColor(sf::Color::Green);
    scoreText.setPosition({50.0f, 50.0f});

    sf::Text scoreAiText(font, "AI Score: 0");
    scoreAiText.setFillColor(sf::Color::Green);
    scoreAiText.setPosition({50.0f, 100.0f});

    sf::Text winnerText(font);
    winnerText.setFillColor(sf::Color::Green);
    winnerText.setPosition({400.0f, 300.0f});

    // box2d world, a "container" to run a physics simulation
    b2WorldDef worldDef = b2DefaultWorldDef();
    worldDef.gravity = {0.0f, 0.0f};
    b2WorldId worldId = b2CreateWorld(&worldDef);

    // SFML render window
    sf::RenderWindow window(sf::VideoMode({WIDTH_IN_PIXELS, HEIGHT_IN_PIXELS}), "Pong clone");
    window.setFramerateLimit(60);

    // NORTH WALL START
    // box2d physics representation
    b2BodyDef northWallBodyDef = b2DefaultBodyDef();
    northWallBodyDef.type = b2_staticBody;
    northWallBodyDef.position = {8.0f, 0.5f};
    b2BodyId northWallId = b2CreateBody(worldId, &northWallBodyDef);

    // box2d shape config and physical geometry
    b2ShapeDef northWalShapeDef = b2DefaultShapeDef();
    b2Polygon northWallGeo = b2MakeBox(8.0f, 0.5f);
    b2ShapeId northWallShapeId = b2CreatePolygonShape(northWallId, &northWalShapeDef, &northWallGeo);

    // sfml graphic to display on the screen
    sf::RectangleShape northWall(b2ToSfml({8.0f * 2.0f, 0.5f * 2.0f}));
    northWall.setOrigin(northWall.getSize() / 2.0f);
    northWall.setFillColor(sf::Color::White);
    northWall.setPosition(b2ToSfml(b2Body_GetPosition(northWallId)));
    // NORTH WALL END

    // SOUTH WALL START
    b2BodyDef southWallBodyDef = b2DefaultBodyDef();
    southWallBodyDef.type = b2_staticBody;
    southWallBodyDef.position = {8.0f, 11.5f};
    b2BodyId southWallId = b2CreateBody(worldId, &southWallBodyDef);

    b2ShapeDef southWalShapeDef = b2DefaultShapeDef();
    b2Polygon southWallGeo = b2MakeBox(8.0f, 0.5f);
    b2ShapeId southWallShapeId = b2CreatePolygonShape(southWallId, &southWalShapeDef, &southWallGeo);

    sf::RectangleShape southWall(b2ToSfml({8.0f * 2.0f, 0.5f * 2.0f}));
    southWall.setOrigin(southWall.getSize() / 2.0f);
    southWall.setFillColor(sf::Color::White);
    southWall.setPosition(b2ToSfml(b2Body_GetPosition(southWallId)));
    // SOUTH WALL END

    // BALL START
    b2BodyDef ballBodyDef = b2DefaultBodyDef();
    ballBodyDef.type = b2_dynamicBody;
    ballBodyDef.position = {8.0f, 6.0f};
    ballBodyDef.linearVelocity = {-5.0f, 0.0f};
    b2BodyId ballId = b2CreateBody(worldId, &ballBodyDef);

    b2ShapeDef ballShapeDef = b2DefaultShapeDef();
    ballShapeDef.enableContactEvents = true; // enable contact events to determine if the ball collided with walls or paddles
    ballShapeDef.enableSensorEvents = true; // enable sensor events to determine if the ball entered a score zone
    b2Circle ballGeo = {};
    ballGeo.radius = 0.5f;
    b2ShapeId ballShapeId = b2CreateCircleShape(ballId, &ballShapeDef, &ballGeo);
    b2Shape_SetRestitution(ballShapeId, 1.0f);

    sf::CircleShape ball(25.0f);
    ball.setOrigin({25.0f, 25.0f});
    ball.setPosition(b2ToSfml(b2Body_GetPosition(ballId)));
    // BALL END

    // PLAYER PADDLE START
    b2BodyDef paddleBodyDef = b2DefaultBodyDef();
    paddleBodyDef.type = b2_kinematicBody;
    paddleBodyDef.position = {1.0f, 8.0f};
    b2BodyId paddleId = b2CreateBody(worldId, &paddleBodyDef);

    b2ShapeDef paddleShapeDef = b2DefaultShapeDef();
    b2Polygon paddleGeo = b2MakeBox(0.5f, 1.5f);
    b2ShapeId paddleShapeId = b2CreatePolygonShape(paddleId, &paddleShapeDef, &paddleGeo);

    sf::RectangleShape paddle({PIXELS_PER_METER, 3.0f * PIXELS_PER_METER});
    paddle.setOrigin(paddle.getSize() / 2.0f);
    paddle.setPosition(b2ToSfml(b2Body_GetPosition(paddleId)));
    // PLAYER PADDLE END

    // AI PADDLE START
    b2BodyDef paddleAiBodyDef = b2DefaultBodyDef();
    paddleAiBodyDef.type = b2_kinematicBody;
    paddleAiBodyDef.position = {15.0f, 6.0f};
    b2BodyId paddleAiId = b2CreateBody(worldId, &paddleAiBodyDef);

    b2ShapeDef paddleAiShapeDef = b2DefaultShapeDef();
    b2Polygon paddleAiGeo = b2MakeBox(0.5f, 1.5f);
    b2ShapeId paddleAiShapeId = b2CreatePolygonShape(paddleAiId, &paddleAiShapeDef, &paddleAiGeo);

    sf::RectangleShape paddleAi({PIXELS_PER_METER, 3.0f * PIXELS_PER_METER});
    paddleAi.setOrigin(paddle.getSize() / 2.0f);
    paddleAi.setPosition(b2ToSfml(b2Body_GetPosition(paddleAiId)));
    // AI PADDLE END

    // WEST SCORE ZONE START
    b2BodyDef westSZBodyDef = b2DefaultBodyDef();
    westSZBodyDef.type = b2_staticBody;
    westSZBodyDef.position = {0.0f, 6.0f};
    b2BodyId westSZId = b2CreateBody(worldId, &westSZBodyDef);

    b2ShapeDef westSZShapeDef = b2DefaultShapeDef();
    westSZShapeDef.enableSensorEvents = true;
    westSZShapeDef.isSensor = true; // no longer blocks other physics entities, but "senses" their presence
    b2Polygon westSZGeo = b2MakeBox(0.5f, 5.0f);
    b2ShapeId westSZShapeId = b2CreatePolygonShape(westSZId, &westSZShapeDef, &westSZGeo);

    // SFML debug graphic
    sf::RectangleShape westSZ(b2ToSfml({0.5f * 2.0f, 5.0f * 2.0f}));
    westSZ.setOrigin(westSZ.getSize() / 2.0f);
    westSZ.setPosition(b2ToSfml(b2Body_GetPosition(westSZId)));
    westSZ.setFillColor(sf::Color::Yellow);
    // WEST SCORE ZONE END

    // EAST SCORE ZONE START
    b2BodyDef eastSZBodyDef = b2DefaultBodyDef();
    eastSZBodyDef.type = b2_staticBody;
    eastSZBodyDef.position = {16.0f, 6.0f};
    b2BodyId eastSZId = b2CreateBody(worldId, &eastSZBodyDef);

    b2ShapeDef eastSZShapeDef = b2DefaultShapeDef();
    eastSZShapeDef.enableSensorEvents = true;
    eastSZShapeDef.isSensor = true;
    b2Polygon eastSZGeo = b2MakeBox(0.5f, 5.0f);
    b2ShapeId eastSZShapeId = b2CreatePolygonShape(eastSZId, &eastSZShapeDef, &eastSZGeo);

    sf::RectangleShape eastSZ(b2ToSfml({0.5f * 2.0f, 5.0f * 2.0f}));
    eastSZ.setOrigin(westSZ.getSize() / 2.0f);
    eastSZ.setPosition(b2ToSfml(b2Body_GetPosition(eastSZId)));
    eastSZ.setFillColor(sf::Color::Yellow);
    // EAST SCORE ZONE END

    int wallHitCounter = 0;

    int playerScore = 0;
    int aiScore = 0;

    while (window.isOpen())
    {
        while (const std::optional<sf::Event> event = window.pollEvent())
        {
            if (event->is<sf::Event::Closed>())
            {
                window.close();
            }
        }

        // This if statement can be seen a simple state machine
        // The game is in two states, playing or end game
        // Show the current state on screen depending on the state conditions
        if (playerScore == 5 || aiScore == 5)
        {
            if (sf::Keyboard::isKeyPressed(sf::Keyboard::Key::R))
            {
                b2Body_SetTransform(ballId, {8.0f, 6.0f}, {0.0f, 0.0f});
                b2Body_SetLinearVelocity(ballId, {-5.0f, 0.0f});
                wallHitCounter = 0;

                playerScore = 0;

                aiScore = 0;

                std::stringstream osAi;
                osAi << "AI Score: " << aiScore;
                scoreAiText.setString(osAi.str());

                std::stringstream os;
                os << "Score: " << playerScore;
                scoreText.setString(os.str());
            }

            if (aiScore > playerScore)
            {
                std::stringstream os;
                os << "You're loser";
                winnerText.setString(os.str());
            }
            else
            {
                std::stringstream os;
                os << "You're winner";
                winnerText.setString(os.str());
            }

            window.clear(sf::Color::Black);

            window.draw(winnerText);
        }
        else
        {
            // Perform all input handling before running the simulation
            // We're setting the objects up to update correctly
            b2Vec2 linearVelocity{0.0f, 0.0f};
            if (sf::Keyboard::isKeyPressed(sf::Keyboard::Key::W))
            {
                linearVelocity.y = -PADDLE_SPEED;
            }
            else if (sf::Keyboard::isKeyPressed(sf::Keyboard::Key::S))
            {
                linearVelocity.y = PADDLE_SPEED;
            }
            b2Body_SetLinearVelocity(paddleId, linearVelocity);

            b2Vec2 aiLinearVelocity{0.0f, 0.0f};
            b2Vec2 ballPosition = b2Body_GetPosition(ballId);
            b2Vec2 paddleAiPosition = b2Body_GetPosition(paddleAiId);
            if (ballPosition.y > paddleAiPosition.y + 0.5f)
            {
                aiLinearVelocity.y = PADDLE_AI_SPEED;
            }
            else if (ballPosition.y < paddleAiPosition.y - 0.5f)
            {
                aiLinearVelocity.y = -PADDLE_AI_SPEED;
            }
            b2Body_SetLinearVelocity(paddleAiId, aiLinearVelocity);

            // Run the physics simulation
            b2World_Step(worldId, 1.0f / 60.0f, 4);

            // Check if anything happened during the simulation step
            b2ContactEvents contactEvents = b2World_GetContactEvents(worldId);
            for (int i = 0; i < contactEvents.beginCount; ++i)
            {
                b2ContactBeginTouchEvent *ev = contactEvents.beginEvents + i;
                if (ev->shapeIdA.index1 == northWallShapeId.index1 ||
                    ev->shapeIdB.index1 == northWallShapeId.index1 ||
                    ev->shapeIdA.index1 == southWallShapeId.index1 ||
                    ev->shapeIdB.index1 == southWallShapeId.index1)
                {
                    ++wallHitCounter;
                }
                if (wallHitCounter > 6)
                {
                    b2Body_SetTransform(ballId, {8.0f, 6.0f}, {0.0f, 0.0f});
                    b2Body_SetLinearVelocity(ballId, {-5.0f, 0.0f});
                    wallHitCounter = 0;
                }
            }

            b2SensorEvents sensorEvents = b2World_GetSensorEvents(worldId);
            for (int i = 0; i < sensorEvents.beginCount; ++i)
            {
                b2SensorBeginTouchEvent *ev = sensorEvents.beginEvents + i;
                if (ev->sensorShapeId.index1 == westSZShapeId.index1)
                {
                    ++aiScore;

                    std::stringstream os;
                    os << "AI Score: " << aiScore;
                    scoreAiText.setString(os.str());

                    b2Body_SetTransform(ballId, {8.0f, 6.0f}, {0.0f, 0.0f});
                    b2Body_SetLinearVelocity(ballId, {-5.0f, 0.0f});
                    wallHitCounter = 0;
                }
                else if (ev->sensorShapeId.index1 == eastSZShapeId.index1)
                {
                    ++playerScore;

                    std::stringstream os;
                    os << "Score: " << playerScore;
                    scoreText.setString(os.str());

                    b2Body_SetTransform(ballId, {8.0f, 6.0f}, {0.0f, 0.0f});
                    b2Body_SetLinearVelocity(ballId, {-5.0f, 0.0f});
                    wallHitCounter = 0;
                }
            }

            // Sync sfml graphics with box2d physics entity positions
            ball.setPosition(b2ToSfml(b2Body_GetPosition(ballId)));
            paddle.setPosition(b2ToSfml(b2Body_GetPosition(paddleId)));
            paddleAi.setPosition(b2ToSfml(b2Body_GetPosition(paddleAiId)));

            // Draw calls
            window.clear(sf::Color::Black);

            window.draw(northWall);
            window.draw(southWall);
            window.draw(ball);

            window.draw(paddle);
            window.draw(paddleAi);

            window.draw(scoreText);
            window.draw(scoreAiText);

            // Debug draws to see where the score zones are
            // window.draw(westSZ);
            // window.draw(eastSZ);
        }
        window.display();
    }

    // Clean up
    b2DestroyShape(eastSZShapeId, false);
    b2DestroyBody(eastSZId);
    b2DestroyShape(westSZShapeId, false);
    b2DestroyBody(westSZId);
    b2DestroyShape(paddleShapeId, false);
    b2DestroyBody(paddleId);
    b2DestroyShape(paddleAiShapeId, false);
    b2DestroyBody(paddleAiId);
    b2DestroyShape(ballShapeId, false);
    b2DestroyBody(ballId);
    b2DestroyShape(southWallShapeId, false);
    b2DestroyBody(southWallId);
    b2DestroyShape(northWallShapeId, false);
    b2DestroyBody(northWallId);
    b2DestroyWorld(worldId);

    return 0;
}

# Associate Coding Exercise: Flashcard Application Enhancement

### Description:

Resmed has developed an internal Flashcard Application to help employees explore and learn about various coding languages. The application offers a straightforward and user-friendly interface for creating, viewing, and managing flashcards. Users can add new flashcards, each containing descriptions of different programming languages,
frameworks, or tools. The initial version of the app includes a set of preloaded flashcards, and users can expand their collection by adding more. Any additions or deletions to the flashcard list does not persist if the application is terminated.

### Tasks:

Your tasks are to extend the functionality of the flashcard application by:
* Implementing a “Delete Flashcard” feature
* Load a default set of flashcards from a REST API
* Make an improvement to the app

#### Delete Flashcard feature
This feature should allow users to delete a flashcard from their collection. You may implement the user experience however you’d like. Like newly added flashcards, any deleted flashcards will not be persisted if the application is terminated.

#### REST API
Please use the following API to receive a list of flashcards to be used in place of the hardcoded dataset. The list received from the API should be the default list of cards shown on app launch.

Method: GET \
Content-Type: application/json \
Url: https://resflash.free.beeceptor.com/v1/flashcards

You can validate the server and response are working correctly with the following curl command: \
`curl -X GET -H 'Content-Type:application/json' -v 'https://resflash.free.beeceptor.com/v1/flashcards'`

#### Subjective App Improvement
At your discretion, make an improvement to the app. This can be anything ranging from a feature enhancement, hardening, architectural improvement, etc. Please be prepared to explain your rationale why the improvement was chosen and what value it brings.

#### Expectations:

* Use Android Studio: Build the app using Android Studio, adhering to Android best practices.
* Presentation: Be prepared to present your solution, development process, and rationale during the interview. Share your screen with the project loaded in Android Studio.
* External resources: Please do not copy, adapt, or reference other people’s solutions, whether from public sources, previous candidates, or online forums. Appropriate use of AI tools is permitted for general research, troubleshooting, or clarifying documentation. However the core design, implementation, and problem-solving approach is expected to be original and of the candidate’s own work. Please be transparent if AI was leveraged at any point in your development process.

This assignment is designed to evaluate your ability to work with an existing codebase and address tasks of suitable complexity. We are interested in understanding your solution process and the solution’s clarity, maintainability, and adherence to best practices.
You will be expected to present your solution during the technical interview. Please have the project ready to share on the screen when joining the interview. We recommend a walkthrough of the solution lasting no more than 15 minutes.

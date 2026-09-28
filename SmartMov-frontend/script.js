let goals = [];

let streak = 0;
let xp = 0;
let level = 1;
let currentResourceType = null;
let currentGoal = null;
let activeGoal = null;
let editingTargetId = null;
let editingResourceId = null;
let resourceModalMode = "add";

let timerInterval = null;

let elapsedSeconds = 0;

let timerPaused = false;

let quoteTimer = null;
let quoteTypingTimer = null;
let quoteRevealTimer = null;
let currentUser = null;
let accountMode = "login";


// // // /// / / / /
const API_BASE_URL = window.SMARTMOV_CONFIG?.apiBaseUrl || "/api";

async function apiRequest(endpoint, options = {}) {
    const token = localStorage.getItem("smartmovToken");

    const headers = {
        ...(options.headers || {})
    };

    if (options.body && !(options.body instanceof FormData)) {
        headers["Content-Type"] = "application/json";
    }

    if (token) {
        headers["Authorization"] = `Bearer ${token}`;
    }

    const response = await fetch(`${API_BASE_URL}${endpoint}`, {
        ...options,
        headers
    });

    let data = null;

    try {
        data = await response.json();
    } catch {
        // Response has no JSON body
    }

    if (!response.ok) {
        throw new Error(
            data?.error || `Request failed with status ${response.status}`
        );
    }

    return data;
}

async function loadTargetsFromBackend() {
    try {
        const [targets, resources] = await Promise.all([
            apiRequest("/targets"),
            apiRequest("/resources")
        ]);

        goals = targets.map(target => ({
            ...target,
            resources: resources.filter(
                resource => resource.targetId === target.id
            )
        }));

        renderGoals();
        updateStats();

        console.log("Targets loaded from backend:", targets);
        console.log("Resources loaded from backend:", resources);

    } catch (error) {
        console.error("Failed to load dashboard data:", error);
        alert("Failed to load your targets and resources.");
    }
}
/// ///// ////////

const SESSION_STORAGE_KEY = "smartmovSession";

localStorage.removeItem("smartmovUsers");

function getProgressStorageKey() {
    const userKey = currentUser?.id || currentUser?.username || currentUser;
    return userKey ? `smartmovProgress:${userKey}` : null;
}

function getUtcDateKey(date = new Date()) {
    return date.toISOString().slice(0, 10);
}

function getUtcDayNumber(dateKey) {
    return Date.parse(`${dateKey}T00:00:00.000Z`) / 86400000;
}

function loadProgressState() {
    const storageKey = getProgressStorageKey();
    let state = {
        streak: 0,
        xp: 0,
        level: 1,
        lastTargetDate: null
    };

    if (storageKey) {
        try {
            state = {
                ...state,
                ...(JSON.parse(localStorage.getItem(storageKey) || "{}"))
            };
        } catch {
            // Use a fresh progression state when stored data is invalid.
        }
    }

    streak = Number(state.streak) || 0;
    xp = Number(state.xp) || 0;
    level = state.level == null
        ? Math.floor(streak / 3) + 1
        : Number(state.level) || 1;
    resetStreakForMissedUtcDay(state.lastTargetDate);
    updateStats();
}

function saveProgressState() {
    const storageKey = getProgressStorageKey();
    if (!storageKey) return;

    localStorage.setItem(storageKey, JSON.stringify({
        streak,
        xp,
        level,
        lastTargetDate: getProgressState().lastTargetDate
    }));
}

function getProgressState() {
    const storageKey = getProgressStorageKey();
    if (!storageKey) return {};

    try {
        return JSON.parse(localStorage.getItem(storageKey) || "{}");
    } catch {
        return {};
    }
}

function recordTargetCreation() {
    const today = getUtcDateKey();
    const previousState = getProgressState();

    if (previousState.lastTargetDate === today) {
        return;
    }

    const previousDate = previousState.lastTargetDate;
    const continuesStreak = previousDate
        && getUtcDayNumber(today) - getUtcDayNumber(previousDate) === 1;

    streak = continuesStreak ? streak + 1 : 1;
    level = Math.floor(streak / 3) + 1;

    localStorage.setItem(getProgressStorageKey(), JSON.stringify({
        streak,
        xp,
        level,
        lastTargetDate: today
    }));
    updateStats();
}

function resetStreakForMissedUtcDay(lastTargetDate) {
    if (!lastTargetDate || lastTargetDate === getUtcDateKey()) {
        return;
    }

    streak = 0;
    const storageKey = getProgressStorageKey();
    if (storageKey) {
        localStorage.setItem(storageKey, JSON.stringify({
            streak,
            xp,
            level,
            lastTargetDate
        }));
    }
}

setInterval(() => {
    const progressState = getProgressState();
    resetStreakForMissedUtcDay(progressState.lastTargetDate);
    updateStats();
}, 60000);

function recordSessionCompletion() {
    xp += 10;
    level = Math.floor(streak / 3) + 1;
    saveProgressState();
    updateStats();
}

const emptyGoalsMessage =
    document.getElementById("emptyGoalsMessage");

const emptyGoalsQuote =
    document.getElementById("emptyGoalsQuote");

const emptyGoalMessages = [
    "An idiot at motion > a genius at rest.",
    "Start Small, Achieve Big."
];

function escapeHtml(value) {
    return String(value)
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#039;");
}

const goalsContainer =
    document.getElementById("goalsContainer");

const createGoalBtn =
    document.getElementById("createGoalBtn");

const accountBtn =
    document.getElementById("accountBtn");

const accountModal =
    document.getElementById("accountModal");

const closeAccountModalBtn =
    document.getElementById("closeAccountModalBtn");

const accountForm =
    document.getElementById("accountForm");

const accountModalTitle =
    document.getElementById("accountModalTitle");

const accountStatus =
    document.getElementById("accountStatus");

const accountEmail =
    document.getElementById("accountEmail");

const accountUsername =
    document.getElementById("accountUsername");

const accountEmailContainer =
    document.getElementById("accountEmailContainer");

const accountPassword =
    document.getElementById("accountPassword");

const accountSubmitBtn =
    document.getElementById("accountSubmitBtn");

const accountModeBtn =
    document.getElementById("accountModeBtn");

const goalModal =
    document.getElementById("goalModal");

const closeModalBtn =
    document.getElementById("closeModalBtn");

const goalForm =
    document.getElementById("goalForm");

const resourceModal =
    document.getElementById("resourceModal");

const closeResourceModalBtn =
    document.getElementById("closeResourceModalBtn");

const resourceForm =
    document.getElementById("resourceForm");

const resourceModalTitle =
    document.getElementById("resourceModalTitle");

const resourceSubmitBtn =
    document.getElementById("resourceSubmitBtn");

const currentResources =
    document.getElementById("currentResources");

const resourceTitle =
    document.getElementById("resourceTitle");

const resourceTypeSelect =
    document.getElementById("resourceType");

const resourceUrl =
    document.getElementById("resourceUrl");

const resourceNote =
    document.getElementById("resourceNote");

const resourceUrlContainer =
    document.getElementById("resourceUrlContainer");

const resourceNoteContainer =
    document.getElementById("resourceNoteContainer");

const resourceFileContainer =
    document.getElementById("resourceFileContainer");

const resourceFile =
    document.getElementById("resourceFile");

const learningModal =
    document.getElementById("learningModal");

const closeLearningModalBtn =
    document.getElementById(
        "closeLearningModalBtn"
    );

const learningModalContent =
    document.querySelector(".learning-modal-content");

const learningDragHandle =
    document.getElementById("learningDragHandle");

const learningGoalTitle =
    document.getElementById(
        "learningGoalTitle"
    );

const sessionStatus =
    document.getElementById("sessionStatus");

const learningResourceTitle =
    document.getElementById(
        "learningResourceTitle"
    );

const timerDisplay =
    document.getElementById(
        "timerDisplay"
    );

const timerTarget =
    document.getElementById(
        "timerTarget"
    );

const pauseTimerBtn =
    document.getElementById(
        "pauseTimerBtn"
    );

const finishTimerBtn =
    document.getElementById(
        "finishTimerBtn"
    );

let learningWidgetOffsetX = 0;
let learningWidgetOffsetY = 0;
let learningWidgetStartX = 0;
let learningWidgetStartY = 0;
let learningWidgetDragging = false;
let learningWidgetPointerId = null;
let sessionPopup = null;

const sessionChannel = typeof BroadcastChannel === "undefined"
    ? null
    : new BroadcastChannel("smartmov-session");

function broadcastSessionState() {
    if (!sessionChannel || !activeGoal) return;

    sessionChannel.postMessage({
        type: "state",
        goalTitle: activeGoal.title,
        resourceTitle: activeGoal.resources.length > 0
            ? activeGoal.resources[0].title
            : "Free study session",
        dailyMinutes: activeGoal.dailyMinutes,
        elapsedSeconds,
        timerPaused
    });
}

function closeSessionPopup() {
    if (sessionPopup && !sessionPopup.closed) {
        sessionPopup.close();
    }
    sessionPopup = null;
}

function openSessionPopup() {
    if (!sessionChannel) return false;

    if (!sessionPopup || sessionPopup.closed) {
        sessionPopup = window.open(
            "session-window.html",
            "SmartMovSession",
            "width=280,height=280,resizable=no"
        );
    }

    if (!sessionPopup) return false;

    sessionPopup.focus();
    broadcastSessionState();
    return true;
}

if (sessionChannel) {
    sessionChannel.addEventListener("message", event => {
        if (!activeGoal) return;

        if (event.data?.type === "ready") {
            broadcastSessionState();
        } else if (event.data?.type === "togglePause") {
            pauseTimerBtn.click();
        } else if (event.data?.type === "finish") {
            finishTimerBtn.click();
        } else if (event.data?.type === "close") {
            closeLearningModalBtn.click();
        }
    });
}

function updateAccountButton() {
    accountBtn.textContent = currentUser
        ? `LOG OUT ➤`
        : "Log in / Sign up";
}

function showAccountStatus(message, isError = true) {
    accountStatus.textContent = message;
    accountStatus.classList.toggle("success", !isError);
}

function openAccountModal(mode = "login") {
    accountMode = mode;
    accountModalTitle.textContent = mode === "login"
        ? "Log in to SmartMov"
        : "Create your SmartMov account";
    accountSubmitBtn.textContent = mode === "login"
        ? "Log in"
        : "Sign up";
    accountModeBtn.textContent = mode === "login"
        ? "Need an account? Sign up"
        : "Already have an account? Log in";
    accountEmailContainer.classList.toggle("hidden", mode === "login");
    accountEmail.required = mode === "signup";
    accountEmail.disabled = mode === "login";
    accountPassword.autocomplete = mode === "login"
        ? "current-password"
        : "new-password";
    accountStatus.textContent = "";
    accountStatus.classList.remove("success");
    accountModal.classList.remove("hidden");
}

accountBtn.addEventListener("click", () => {
    if (currentUser) {
        currentUser = null;
        goals = [];
        streak = 0;
        xp = 0;
        level = 1;
        localStorage.removeItem(SESSION_STORAGE_KEY);
        localStorage.removeItem("smartmovToken");
        localStorage.removeItem("smartmovUser");
        updateStats();
        renderGoals();
        updateAccountButton();
        return;
    }

    openAccountModal();
});

closeAccountModalBtn.addEventListener("click", () => {
    accountModal.classList.add("hidden");
});

accountModeBtn.addEventListener("click", () => {
    openAccountModal(accountMode === "login" ? "signup" : "login");
});


// // // // /// /// // // / // /
accountForm.addEventListener("submit", async (event) => {
    event.preventDefault();

    const username = accountUsername.value.trim();
    const email = accountEmail.value.trim().toLowerCase();
    const password = accountPassword.value;

    if (accountMode === "signup") {
        try {
            await apiRequest("/auth/register", {
                method: "POST",
                body: JSON.stringify({ username, email, password })
            });

            accountForm.reset();
            openAccountModal("login");
            accountUsername.value = username;
            showAccountStatus("Account created. You can now log in.", false);
        } catch (error) {
            showAccountStatus(error.message || "Sign up failed.");
        }
    } else {
        try {
            const response = await apiRequest("/auth/login", {
                method: "POST",
                body: JSON.stringify({
                    username,
                    password
                })
            });

            localStorage.setItem("smartmovToken", response.token);
            localStorage.setItem("smartmovUser", JSON.stringify({
                id: response.userId,
                username: response.username,
                email: response.email
            }));

            currentUser = {
    id: response.userId,
    username: response.username,
    email: response.email
};
            updateAccountButton();
            localStorage.setItem(SESSION_STORAGE_KEY, response.username);
            loadProgressState();

accountModal.classList.add("hidden");

await loadTargetsFromBackend();

console.log("Backend login successful:", response);
        } catch (error) {
            showAccountStatus(error.message || "Login failed.");
        }
    }
});

// // // // // // / / // 

if (localStorage.getItem("smartmovToken")) {
    try {
        currentUser = JSON.parse(localStorage.getItem("smartmovUser"));
        loadProgressState();
        loadTargetsFromBackend();
        updateAccountButton();
        updateStats();
    } catch {
        localStorage.removeItem("smartmovToken");
        localStorage.removeItem("smartmovUser");
        updateAccountButton();
        updateStats();
    }
} else {
    updateAccountButton();
    updateStats();
}


// open goal form
createGoalBtn.addEventListener("click", () => {

    if (!currentUser) {
        openAccountModal();
        return;
    }

    editingTargetId = null;
    goalModal.classList.remove("hidden");

});

closeModalBtn.addEventListener("click", () => {

    goalModal.classList.add("hidden");

});
// close goal form


// creating goal
goalForm.addEventListener("submit", async (event) => {
    event.preventDefault();

    const title =
        document.getElementById("goalTitle").value.trim();

    const description =
        document.getElementById("goalDescription").value.trim();

    const category =
        document.getElementById("goalCategory").value.trim();

    const dailyMinutes =
        Number(document.getElementById("goalMinutes").value);

    if (!title) {
        alert("Please enter a target title.");
        return;
    }

    if (!category) {
        alert("Please enter a category.");
        return;
    }

    if (!Number.isFinite(dailyMinutes) || dailyMinutes < 10) {
        alert("Daily target must be at least 10 minutes.");
        return;
    }

    const isCreatingTarget = !editingTargetId;

    try {
        const target = await apiRequest(
            editingTargetId ? `/targets/${editingTargetId}` : "/targets",
            {
            method: editingTargetId ? "PUT" : "POST",
            body: JSON.stringify({
                title,
                description,
                category,
                dailyMinutes
            })
            }
        );

        if (editingTargetId) {
            const goalIndex = goals.findIndex(goal => goal.id === editingTargetId);
            goals[goalIndex] = { ...goals[goalIndex], ...target };
        } else {
            goals.push({
                ...target,
                resources: []
            });
        }

        if (isCreatingTarget) {
            recordTargetCreation();
        }

        renderGoals();

        goalForm.reset();
        goalModal.classList.add("hidden");
        editingTargetId = null;

    } catch (error) {
        console.error("Failed to create target:", error);
        alert(error.message || "Failed to create target.");
    }
});

async function editTarget(goalId) {
    const goal = goals.find(item => item.id === goalId);
    if (!goal) return;

    editingTargetId = goalId;
    document.getElementById("goalTitle").value = goal.title || "";
    document.getElementById("goalDescription").value = goal.description || "";
    document.getElementById("goalCategory").value = goal.category || "";
    document.getElementById("goalMinutes").value = goal.dailyMinutes || 10;
    goalModal.classList.remove("hidden");
}

async function deleteTarget(goalId) {
    if (!confirm("Delete this target and all its resources?")) return;

    try {
        await apiRequest(`/targets/${goalId}`, { method: "DELETE" });
        clearPausedSession(goalId);
        goals = goals.filter(goal => goal.id !== goalId);
        renderGoals();
    } catch (error) {
        alert(error.message || "Failed to delete target.");
    }
}

function pausedSessionStorageKey() {
    const userKey = currentUser?.id || currentUser?.username || currentUser;
    return userKey ? `smartmovPausedSession:${userKey}` : null;
}

function getPausedSession() {
    const storageKey = pausedSessionStorageKey();
    if (!storageKey) return null;

    try {
        return JSON.parse(localStorage.getItem(storageKey) || "null");
    } catch {
        return null;
    }
}

function savePausedSession(goalId, seconds) {
    const storageKey = pausedSessionStorageKey();
    if (!storageKey) return;

    localStorage.setItem(storageKey, JSON.stringify({
        targetId: goalId,
        elapsedSeconds: seconds,
        pausedAt: new Date().toISOString()
    }));
}

function clearPausedSession(goalId = null) {
    const storageKey = pausedSessionStorageKey();
    if (!storageKey) return;

    const savedSession = getPausedSession();
    if (!goalId || savedSession?.targetId === goalId) {
        localStorage.removeItem(storageKey);
    }
}

//closing goal


//displaying goals
function renderGoals() {

    goalsContainer.innerHTML = "";

    if (goals.length > 0) {
        stopQuoteAnimation();
        emptyGoalsMessage.classList.add("hidden");
    } else {
        showEmptyGoalMessages();
    }


    const pausedSession = getPausedSession();

    goals.forEach((goal) => {
        const hasResources = goal.resources.length > 0;

        const pausedSeconds = pausedSession?.targetId === goal.id
            ? Number(pausedSession.elapsedSeconds) || 0
            : 0;
        const displayedMinutes = Math.min(
            (goal.completedMinutes || 0) + pausedSeconds / 60,
            goal.dailyMinutes
        );

        const percentage =
            Math.min(
                (displayedMinutes /
                    goal.dailyMinutes) * 100,
                100
            );


        const goalElement =
            document.createElement("div");

        goalElement.className = "goal-card";


        goalElement.innerHTML = `

            <button
                class="delete-target-btn"
                title="Delete target"
                aria-label="Delete target"
                onclick="deleteTarget(${goal.id})"
            >
                X
            </button>

            <div class="goal-header">

                <div>
                    <h3>${escapeHtml(goal.title)}</h3>
                    <p class="goal-category" style="color: var(--primary); font-weight: 800; text-transform: uppercase;">
                        ${escapeHtml(goal.category || "General")}
                    </p>
                </div>

            </div>

            ${goal.description
                ? `<p class="goal-description">${escapeHtml(goal.description)}</p>`
                : ""}

            <div class="progress-container">
                <div class="progress-bar">
                    <div
                        class="progress"
                        data-goal-progress="${goal.id}"
                        style="width: ${percentage}%"
                    ></div>
                </div>
                <p class="progress-percentage" data-goal-progress-percent="${goal.id}">${Math.round(percentage)}%</p>
                <p data-goal-minutes="${goal.id}">${Math.floor(displayedMinutes)} of ${goal.dailyMinutes} minutes completed</p>
            </div>

            <div class="goal-footer">
                <span style="color: var(--primary); font-weight: 800;">
                    ${pausedSession?.targetId === goal.id
                        ? "Paused session"
                        : goal.completedToday ? "Completed today" : "Keep going"}
                </span>
                <button
                    class="start-btn"
                    onclick="startLearning(${goal.id})"
                    ${goal.completedToday ? "disabled" : ""}
                >
                    ${goal.completedToday ? "Completed" : "Start session"}
                </button>
            </div>

            <div class="resources">

    <div class="resources-header">

        <strong>Resources</strong>

        <span class="resource-header-actions">
            <button
                class="small-btn"
                onclick="openAddResourceModal(${goal.id})"
            >
                Add resource
            </button>
            <button
                class="small-btn edit-resources-btn ${hasResources ? "has-resources" : ""}"
                ${hasResources ? `onclick="openEditResourcesModal(${goal.id})"` : "disabled"}
            >
                Edit resources
            </button>
        </span>

    </div>


    <div class="resource-list">

    ${
        goal.resources.length === 0

        ? `
            <p class="no-resources">
                No resources added yet.
            </p>
          `

        : goal.resources.map(resource => {

            let icon = "🤍";
            const resourceDetails = `
                <span class="resource-details">
                    <span>${escapeHtml(resource.title)}</span>
                    ${resource.content
                        ? `<span class="resource-note-preview">${escapeHtml(resource.content)}</span>`
                        : ""}
                </span>
            `;

            if (resource.type === "YOUTUBE") {
                icon = "🤍";
            }

            if (resource.type === "WEBSITE") {
                icon = "🤍";
            }

            if (resource.type === "FILE") {
                icon = "🤍";
            }


            if (
                resource.type === "YOUTUBE" ||
                resource.type === "WEBSITE"
            ) {

                return `

                    <a
                        class="resource-item resource-link"
                        href="${resource.url}"
                        target="_blank"
                        rel="noopener noreferrer"
                    >

                        <span>${icon}</span>

                        ${resourceDetails}

                    </a>

                `;

            }


            if (
                resource.type === "FILE"
            ) {

                return `

                    <a
                        class="resource-item resource-link"
                        href="${API_BASE_URL}/resources/file/${resource.id}"
                        onclick="event.preventDefault(); openFileResource(${resource.id})"
                    >

                        <span>${icon}</span>

                        ${resourceDetails}

                    </a>

                `;

            }


            return `

                <div
                    class="resource-item note-resource"
                    onclick="showNote(${resource.id}, ${goal.id})"
                >

                    <span>📝</span>

                    ${resourceDetails}

                </div>

            `;

        }).join("")
    }

</div>

</div>

        `;


        goalsContainer.appendChild(goalElement);

    });

}

function showEmptyGoalMessages() {
    stopQuoteAnimation();
    emptyGoalsMessage.classList.remove("hidden");

    let messageIndex = 0;

    const showMessage = (message) => {
        clearInterval(quoteTypingTimer);
        clearTimeout(quoteRevealTimer);
        emptyGoalsQuote.classList.remove("quote-visible");
        emptyGoalsQuote.textContent = "";

        quoteRevealTimer = setTimeout(() => {
            emptyGoalsQuote.classList.add("quote-visible");
            emptyGoalsQuote.classList.add("quote-caret");
            let characterIndex = 0;

            quoteTypingTimer = setInterval(() => {
                emptyGoalsQuote.textContent = message.slice(0, characterIndex + 1);
                characterIndex++;

                if (characterIndex === message.length) {
                    clearInterval(quoteTypingTimer);
                    quoteTypingTimer = null;
                    emptyGoalsQuote.classList.remove("quote-caret");
                }
            }, 85);
        }, 250);
    };

    showMessage(emptyGoalMessages[messageIndex]);

    quoteTimer = setInterval(() => {
        messageIndex++;

        if (messageIndex < emptyGoalMessages.length) {
            showMessage(emptyGoalMessages[messageIndex]);
            return;
        }

        clearInterval(quoteTimer);
        quoteTimer = null;
        showMessage("Let's get started . . .");
    }, 5000);
}

function stopQuoteAnimation() {
    clearInterval(quoteTimer);
    clearInterval(quoteTypingTimer);
    clearTimeout(quoteRevealTimer);
    quoteTimer = null;
    quoteTypingTimer = null;
    quoteRevealTimer = null;
}

renderGoals();
// closing goals


// adding learning timer
function startLearning(goalId) {

    const goal =
        goals.find(
            goal => goal.id === goalId
        );


    if (!goal) {
        return;
    }


    activeGoal = goal;

    const savedSession = getPausedSession();
    const hasPausedSession = savedSession?.targetId === goalId;

    elapsedSeconds = hasPausedSession
        ? Number(savedSession.elapsedSeconds) || 0
        : 0;

    timerPaused = hasPausedSession;
    sessionStatus.textContent = hasPausedSession ? "Paused session" : "";
    sessionStatus.classList.toggle("visible", hasPausedSession);
    pauseTimerBtn.textContent = hasPausedSession
        ? "▶ Resume"
        : "⏸ Pause";


    learningGoalTitle.textContent =
        goal.title;


    learningResourceTitle.textContent =
        goal.resources.length > 0
            ? goal.resources[0].title
            : "Free study session";


    timerTarget.textContent =
        `Target: ${goal.dailyMinutes} minutes`;


    updateTimerDisplay();


    const popupOpened = openSessionPopup();
    learningModal.classList.toggle("hidden", popupOpened);


    startTimer();
    updateLearningProgress();
    broadcastSessionState();

}
// closing timer

// adding timer function
function startTimer() {

    clearInterval(timerInterval);


    timerInterval =
        setInterval(() => {

            if (timerPaused) {
                return;
            }


            elapsedSeconds++;


            updateTimerDisplay();
            updateLearningProgress();
            broadcastSessionState();


        }, 1000);

}
// closing timer function

//displayin timer
function updateTimerDisplay() {

    const hours =
        Math.floor(
            elapsedSeconds / 3600
        );


    const minutes =
        Math.floor(
            (elapsedSeconds % 3600) / 60
        );


    const seconds =
        elapsedSeconds % 60;


    timerDisplay.textContent =

        String(hours).padStart(2, "0")
        + ":" +

        String(minutes).padStart(2, "0")
        + ":" +

        String(seconds).padStart(2, "0");

}
//closing timer display

learningDragHandle.addEventListener("pointerdown", event => {
    if (event.target.closest("button")) return;

    learningWidgetDragging = true;
    learningWidgetPointerId = event.pointerId;
    learningWidgetStartX = event.clientX;
    learningWidgetStartY = event.clientY;
    learningDragHandle.setPointerCapture(event.pointerId);
    learningModalContent.classList.add("dragging");
});

learningDragHandle.addEventListener("pointermove", event => {
    if (!learningWidgetDragging || event.pointerId !== learningWidgetPointerId) {
        return;
    }

    learningWidgetOffsetX += event.clientX - learningWidgetStartX;
    learningWidgetOffsetY += event.clientY - learningWidgetStartY;
    learningWidgetStartX = event.clientX;
    learningWidgetStartY = event.clientY;
    learningModalContent.style.transform =
        `translate(${learningWidgetOffsetX}px, ${learningWidgetOffsetY}px)`;
});

function stopLearningWidgetDrag(event) {
    if (!learningWidgetDragging || event.pointerId !== learningWidgetPointerId) {
        return;
    }

    learningWidgetDragging = false;
    learningWidgetPointerId = null;
    learningModalContent.classList.remove("dragging");
}

learningDragHandle.addEventListener("pointerup", stopLearningWidgetDrag);
learningDragHandle.addEventListener("pointercancel", stopLearningWidgetDrag);

function updateLearningProgress() {
    if (!activeGoal) return;

    const displayedMinutes = Math.min(
        (activeGoal.completedMinutes || 0) + elapsedSeconds / 60,
        activeGoal.dailyMinutes
    );
    const percentage = Math.min(
        (displayedMinutes / activeGoal.dailyMinutes) * 100,
        100
    );
    const progressElement = document.querySelector(
        `[data-goal-progress="${activeGoal.id}"]`
    );
    const minutesElement = document.querySelector(
        `[data-goal-minutes="${activeGoal.id}"]`
    );
    const progressPercentElement = document.querySelector(
        `[data-goal-progress-percent="${activeGoal.id}"]`
    );

    if (progressElement) {
        progressElement.style.width = `${percentage}%`;
    }
    if (progressPercentElement) {
        progressPercentElement.textContent = `${Math.round(percentage)}%`;
    }
    if (minutesElement) {
        minutesElement.textContent = `${Math.floor(displayedMinutes)} of ${activeGoal.dailyMinutes} minutes completed`;
    }
}

function renderCurrentResources() {
    if (!currentGoal) {
        currentResources.innerHTML = "";
        return;
    }

    currentResources.innerHTML = `
        <h3>Current resources</h3>
        ${currentGoal.resources.length === 0
            ? `<p class="no-resources">No resources added yet.</p>`
            : currentGoal.resources.map(resource => `
                <div class="managed-resource">
                    <span>${escapeHtml(resource.title)}</span>
                    <span class="managed-resource-actions">
                        <button type="button" onclick="editResource(${resource.id}, ${currentGoal.id})" style="color: white; background-color: black">Edit</button>
                        <button type="button" onclick="deleteResource(${resource.id}, ${currentGoal.id})" style="color: white; background-color: black">Delete</button>
                    </span>
                </div>
            `).join("")}
    `;
}

pauseTimerBtn.addEventListener(
    "click",
    () => {
        timerPaused = !timerPaused;
        pauseTimerBtn.textContent = timerPaused
            ? "▶ Resume"
            : "⏸ Pause";
        if (timerPaused) {
            savePausedSession(activeGoal.id, elapsedSeconds);
        } else {
            clearPausedSession(activeGoal.id);
        }
        sessionStatus.textContent = timerPaused ? "Paused session" : "";
        sessionStatus.classList.toggle("visible", timerPaused);
        renderGoals();
        broadcastSessionState();
    }
);

//finish session
finishTimerBtn.addEventListener(
    "click",
    () => {

        if (!activeGoal) {
            return;
        }


        const minutesStudied =
            Math.floor(
                elapsedSeconds / 60
            );


        if (elapsedSeconds < 10 * 60) {
            alert("Complete at least 10 minutes before finishing the session.");
            return;
        }

        apiRequest(`/targets/${activeGoal.id}/progress`, {
            method: "POST",
            body: JSON.stringify({ minutes: minutesStudied })
        }).then(updatedGoal => {
            const goalIndex = goals.findIndex(goal => goal.id === activeGoal.id);
            if (goalIndex !== -1) {
                goals[goalIndex] = {
                    ...goals[goalIndex],
                    ...updatedGoal
                };
            }

            recordSessionCompletion();

            clearInterval(timerInterval);
            clearPausedSession(activeGoal.id);
            closeSessionPopup();
            learningModal.classList.add("hidden");
            renderGoals();
            activeGoal = null;
            updateStats();
        }).catch(error => {
            alert(error.message || "Failed to save your progress.");
        });

    }
);
//closing finish session

//closing timer
closeLearningModalBtn.addEventListener(
    "click",
    () => {

        clearInterval(timerInterval);

        if (activeGoal) {
            savePausedSession(activeGoal.id, elapsedSeconds);
            broadcastSessionState();
        }

        closeSessionPopup();

        learningModal.classList.add(
            "hidden"
        );

        sessionStatus.textContent = "";
        sessionStatus.classList.remove("visible");
        renderGoals();
        activeGoal = null;

    }
);



//statistics
function updateStats() {

    document.getElementById("streak").textContent =
        streak;

    document.getElementById("xp").textContent =
        xp;

    document.getElementById("level").textContent =
        level;

}
// closing statistics

//creating resource modal
function configureResourceFields(type, isEditing = false) {
    const normalizedType = type === "LINK" ? "WEBSITE" : type;
    const usesUrl = normalizedType === "YOUTUBE"
        || normalizedType === "WEBSITE";
    const usesNote = ["YOUTUBE", "WEBSITE", "FILE", "NOTE"].includes(normalizedType);
    const usesFile = normalizedType === "FILE";

    resourceUrlContainer.classList.toggle("hidden", !usesUrl);
    resourceNoteContainer.classList.toggle("hidden", !usesNote);
    resourceFileContainer.classList.toggle("hidden", !usesFile);

    resourceUrl.required = usesUrl;
    resourceNote.required = false;
    resourceFile.required = usesFile && !isEditing;

    if (normalizedType === "YOUTUBE") {
        resourceUrl.placeholder = "https://youtube.com/watch?v=...";
    } else if (normalizedType === "WEBSITE") {
        resourceUrl.placeholder = "https://example.com";
    }
}

function openResourceModal(type, mode = "add") {

    removeLegacyNoteTypeOption();
    resourceModalMode = mode;
    currentResourceType = type === "LINK" ? "WEBSITE" : type;
    editingResourceId = null;
    resourceTypeSelect.disabled = false;
    resourceTypeSelect.value = currentResourceType;
    resourceSubmitBtn.textContent = "Add Resource";

    resourceTitle.value = "";
    resourceUrl.value = "";
    resourceNote.value = "";
    resourceFile.value = "";


    resourceModalTitle.textContent = mode === "edit"
        ? "Edit Resources"
        : currentResourceType === "YOUTUBE"
            ? "Add YouTube Video"
            : currentResourceType === "WEBSITE"
                ? "Add Website"
                : currentResourceType === "FILE"
                    ? "Add PDF / Document"
                    : "Add Personal Note";
    configureResourceFields(currentResourceType);
    currentResources.classList.toggle("hidden", mode !== "edit");
    resourceForm.classList.toggle("hidden", mode === "edit");


    resourceModal.classList.remove("hidden");
    renderCurrentResources();

}

resourceTypeSelect.addEventListener("change", () => {
    openResourceModal(resourceTypeSelect.value, resourceModalMode);
});

function removeLegacyNoteTypeOption() {
    resourceTypeSelect.querySelector('[data-legacy-note="true"]')?.remove();
}

function editResource(resourceId, goalId) {
    const goal = goals.find(item => item.id === goalId);
    const resource = goal?.resources.find(item => item.id === resourceId);
    if (!goal || !resource) return;

    currentGoal = goal;
    resourceModalMode = "edit";
    currentResourceType = resource.type;
    editingResourceId = resource.id;
    if (resource.type === "NOTE" && !resourceTypeSelect.querySelector('option[value="NOTE"]')) {
        const legacyNoteOption = document.createElement("option");
        legacyNoteOption.value = "NOTE";
        legacyNoteOption.textContent = "Personal note (existing)";
        legacyNoteOption.dataset.legacyNote = "true";
        resourceTypeSelect.append(legacyNoteOption);
    }
    resourceTypeSelect.value = resource.type;
    resourceTypeSelect.disabled = true;
    resourceSubmitBtn.textContent = "Save changes";
    resourceTitle.value = resource.title || "";
    resourceUrl.value = resource.url || "";
    resourceNote.value = resource.content || "";
    configureResourceFields(resource.type, true);
    resourceModalTitle.textContent = "Edit Resource";
    currentResources.classList.remove("hidden");
    resourceForm.classList.remove("hidden");
    resourceModal.classList.remove("hidden");
    renderCurrentResources();
}

async function deleteResource(resourceId, goalId) {
    if (!confirm("Delete this resource?")) return;

    try {
        await apiRequest(`/resources/${resourceId}`, { method: "DELETE" });
        const goal = goals.find(item => item.id === goalId);
        if (goal) {
            goal.resources = goal.resources.filter(resource => resource.id !== resourceId);
        }
        renderCurrentResources();
        renderGoals();
    } catch (error) {
        alert(error.message || "Failed to delete resource.");
    }
}

async function openFileResource(resourceId) {
    try {
        const response = await fetch(`${API_BASE_URL}/resources/file/${resourceId}`, {
            headers: {
                Authorization: `Bearer ${localStorage.getItem("smartmovToken")}`
            }
        });
        if (!response.ok) throw new Error("Unable to open this file.");

        const blob = await response.blob();
        window.open(URL.createObjectURL(blob), "_blank");
    } catch (error) {
        alert(error.message || "Unable to open this file.");
    }
}

// closing resources modal
closeResourceModalBtn.addEventListener(
    "click",
    () => {

        resourceModal.classList.add("hidden");
        removeLegacyNoteTypeOption();

    }
);

//adding resource to goal
resourceForm.addEventListener(
    "submit",
    async (event) => {
        event.preventDefault();

        if (!currentGoal) {
            alert("Please select a target first.");
            return;
        }

        const title = resourceTitle.value.trim();

        if (!title) {
            alert("Please enter a resource title.");
            return;
        }

        const resourceType = currentResourceType === "LINK"
            ? "WEBSITE"
            : currentResourceType;

        const requestBody = {
            title,
            type: resourceType,
            targetId: currentGoal.id
        };

        if (
            currentResourceType === "YOUTUBE" ||
            currentResourceType === "LINK" ||
            currentResourceType === "WEBSITE"
        ) {
            const url = resourceUrl.value.trim();

            if (!url) {
                alert("Please enter a URL.");
                return;
            }

            requestBody.url = url;
        }

        const content = resourceNote.value.trim();
        requestBody.content = content;

        try {
            let resource;

            if (currentResourceType === "FILE" && !editingResourceId) {
                if (!resourceFile.files[0]) {
                    alert("Please choose a PDF or document.");
                    return;
                }

                const formData = new FormData();
                formData.append("title", title);
                formData.append("file", resourceFile.files[0]);
                formData.append("targetId", currentGoal.id);
                formData.append("content", content);
                resource = await apiRequest("/resources/file", {
                    method: "POST",
                    body: formData
                });
            } else if (editingResourceId) {
                resource = await apiRequest(`/resources/${editingResourceId}`, {
                    method: "PUT",
                    body: JSON.stringify(requestBody)
                });
            } else {
                resource = await apiRequest("/resources", {
                    method: "POST",
                    body: JSON.stringify(requestBody)
                });
            }

            console.log("Resource created in backend:", resource);

            if (editingResourceId) {
                const resourceIndex = currentGoal.resources.findIndex(
                    item => item.id === editingResourceId
                );
                currentGoal.resources[resourceIndex] = resource;
            } else {
                currentGoal.resources.push(resource);
            }

            renderGoals();

            resourceForm.reset();
            resourceTypeSelect.disabled = false;
            resourceSubmitBtn.textContent = "Add Resource";
            resourceModal.classList.add("hidden");
            removeLegacyNoteTypeOption();
            editingResourceId = null;

        } catch (error) {
            console.error("Failed to create resource:", error);
            alert(error.message || "Failed to create resource.");
        }
    }
);
// finsih adding resource


function selectGoalForResource(goalId) {

    openAddResourceModal(goalId);
}

function openAddResourceModal(goalId) {

    currentGoal =
        goals.find(
            goal => goal.id === goalId
        );

    if (!currentGoal) {
        return;
    }

    openResourceModal("YOUTUBE", "add");

}

function openEditResourcesModal(goalId) {
    currentGoal = goals.find(goal => goal.id === goalId);

    if (!currentGoal) {
        return;
    }

    openResourceModal("YOUTUBE", "edit");
    renderCurrentResources();

}

function showNote(resourceId, goalId) {

    const goal = goals.find(item => item.id === goalId);
    if (!goal) {
        return;
    }


    const resource =
        goal.resources.find(
            resource => resource.id === resourceId
        );


    if (!resource) {
        return;
    }


    alert(
        resource.title +
        "\n\n" +
        resource.content
    );

}

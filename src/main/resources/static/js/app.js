const form = document.getElementById("f");
const video = document.getElementById("v");

const downloadBtn = document.getElementById("downloadBtn");
const snapshotBtn = document.getElementById("snapshotBtn");

const forward10SecBtn = document.getElementById("forward10SecBtn");
const forward1MinBtn = document.getElementById("forward1MinBtn");
const forward5MinBtn = document.getElementById("forward5MinBtn");
const backward10SecBtn = document.getElementById("backward10SecBtn");
const backward1MinBtn = document.getElementById("backward1MinBtn");
const backward5MinBtn = document.getElementById("backward5MinBtn");

const dlStatus = document.getElementById("dlStatus");
const dlText = document.getElementById("dlText");
const dlDots = document.getElementById("dlDots");

const snapStatus = document.getElementById("snapStatus");
const snapText = document.getElementById("snapText");

const canvas = document.getElementById("snapCanvas");
const ctx = canvas.getContext("2d");
let timeStartLastPreview = null;

function showBanner(bannerEl, textEl, text, dotsEl, dotsRunning, autoHideMs) {
    textEl.textContent = text;

    bannerEl.style.display = "flex";

    if (dotsEl) {
        if (dotsRunning) {
            dotsEl.classList.remove("paused");
        } else {
            dotsEl.classList.add("paused");
        }
    }

    if (autoHideMs && autoHideMs > 0) {
        setTimeout(() => {
            bannerEl.style.display = "none";
        }, autoHideMs);
    }
}

// PREVIEW
form.addEventListener("submit", (e) => {
    e.preventDefault();
    const params = new URLSearchParams(new FormData(form));
    video.src = "/api/clip.mp4?" + params.toString();
    video.load();
    video.play().catch(() => {
    });
    timeStartLastPreview = new Date();

});



forward10SecBtn.addEventListener("click", () => shiftFormTimeBySeconds(10));
forward1MinBtn.addEventListener("click", () => shiftFormTimeBySeconds(60));
forward5MinBtn.addEventListener("click", () => shiftFormTimeBySeconds(300));
backward10SecBtn.addEventListener("click", () => shiftFormTimeBySeconds(-10));
backward1MinBtn.addEventListener("click", () => shiftFormTimeBySeconds(-60));
backward5MinBtn.addEventListener("click", () => shiftFormTimeBySeconds(-300));

function throttleTime() {
    return timeStartLastPreview.getTime() + 500 <= new Date();
}

function shiftFormTimeBySeconds(offsetSeconds) {
    const dateStr = form.elements["date"].value;
    const timeStr = form.elements["time"].value;

    const [y, m, d] = dateStr.split("-").map(Number);
    const [hh = 0, mm = 0, ss = 0] = timeStr.split(":").map(Number);
    const dt = new Date(y, m - 1, d, hh, mm, ss, 0);

    if (Number.isNaN(dt.getTime())) return;

    // Add time
    dt.setSeconds(dt.getSeconds() + offsetSeconds);

    // Check time is not if future, set to now
    const now = new Date();
    if (dt < now) {
        const pad2 = (n) => String(n).padStart(2, "0");
        form.elements["date"].value = `${dt.getFullYear()}-${pad2(dt.getMonth() + 1)}-${pad2(dt.getDate())}`;
        form.elements["time"].value = `${pad2(dt.getHours())}:${pad2(dt.getMinutes())}:${pad2(dt.getSeconds())}`;
        // console.log(form.time);
        if(throttleTime()) {
            form.requestSubmit();
        }
        else {
            console.log("too fast");
        }

    } else {
        console.log("time can't be later than now");
    }

}

// DOWNLOAD
downloadBtn.addEventListener("click", async () => {
    const params = new URLSearchParams(new FormData(form));
    params.set("download", "true");

    const url = "/api/clip.mp4?" + params.toString();

    showBanner(dlStatus, dlText, "Downloading", dlDots, true, 0);
    downloadBtn.disabled = true;

    try {
        const response = await fetch(url);
        if (!response.ok) {
            throw new Error("HTTP " + response.status);
        }

        const blob = await response.blob();

        const disposition = response.headers.get("Content-Disposition") || "";
        const match = disposition.match(/filename="([^"]+)"/i);
        const filename = match ? match[1] : "clip.mp4";

        const objectUrl = URL.createObjectURL(blob);
        const a = document.createElement("a");
        a.href = objectUrl;
        a.download = filename;
        document.body.appendChild(a);
        a.click();
        a.remove();
        URL.revokeObjectURL(objectUrl);

        showBanner(dlStatus, dlText, "Download available", dlDots, false, 1500);

    } catch (err) {
        console.error(err);

        showBanner(dlStatus, dlText, "Download failed", dlDots, false, 2500);

    } finally {
        downloadBtn.disabled = false;
    }
});

// SNAPSHOT
snapshotBtn.addEventListener("click", () => {
    if (!video.src) {
        showBanner(snapStatus, snapText, "No preview loaded", null, false, 1500);
        return;
    }

    if (video.readyState < 2) {
        showBanner(snapStatus, snapText, "Preview not ready yet", null, false, 1500);
        return;
    }

    const width = video.videoWidth;
    const height = video.videoHeight;

    if (!width || !height) {
        showBanner(snapStatus, snapText, "No video frame available", null, false, 1500);
        return;
    }

    canvas.width = width;
    canvas.height = height;
    ctx.drawImage(video, 0, 0, width, height);

    canvas.toBlob((blob) => {
        if (!blob) {
            showBanner(snapStatus, snapText, "Snapshot failed", null, false, 1500);
            return;
        }

        const now = new Date();
        const fileName = `snapshot-${now.toISOString().replaceAll(":", "-")}.png`;

        const objectUrl = URL.createObjectURL(blob);
        const a = document.createElement("a");
        a.href = objectUrl;
        a.download = fileName;
        document.body.appendChild(a);
        a.click();
        a.remove();
        URL.revokeObjectURL(objectUrl);

        showBanner(snapStatus, snapText, "Snapshot available", null, false, 1500);

    }, "image/png");

//     FORWARD

});
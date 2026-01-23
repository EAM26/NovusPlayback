const form = document.getElementById("f");
const video = document.getElementById("v");

const downloadBtn = document.getElementById("downloadBtn");
const snapshotBtn = document.getElementById("snapshotBtn");
const forward10Btn = document.getElementById("forward10Btn");

const dlStatus = document.getElementById("dlStatus");
const dlText = document.getElementById("dlText");
const dlDots = document.getElementById("dlDots");

const snapStatus = document.getElementById("snapStatus");
const snapText = document.getElementById("snapText");

const canvas = document.getElementById("snapCanvas");
const ctx = canvas.getContext("2d");

let clipStartDateTime = null;

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
    clipStartDateTime = new Date();
});

forward10Btn.addEventListener("click", async () => {
    console.log("Forward button clicked");
    // const params = new URLSearchParams(new FormData(form));
    // const formData = Object.fromEntries(params.entries());
    // const timestamp = new Date(formData.date + " " + formData.time);
    // const delta = 10 * 1000;
    // const timeButtonClicked = new Date();
    // if(!clipStartDateTime) clipStartDateTime = new Date();
    // console.log("start time: " + clipStartDateTime);
    // console.log("clicked time: " + timeButtonClicked);
    //
    // const timePlayed = timeButtonClicked - clipStartDateTime;
    // // clipStartDateTime = new Date();
    // let result = new Date(timestamp.getTime() + delta + timePlayed);
    //
    // const correctedDate = formatDatetime(result);
    //
    // params.set("date", correctedDate[0]);
    // params.set("time", correctedDate[1]);
    //
    // video.src = "/api/clip.mp4?" + params.toString();
    // video.load();
    // video.play().catch(() => {
    //
    //
    //
    // });
    // console.log("start time: " + clipStartDateTime);
    // console.log(correctedDate[0]);
    // console.log(correctedDate[1]);

})

// function formatDatetime(dateIso) {
//     const offset = dateIso.getTimezoneOffset()
//     const dateCorrected = new Date(dateIso.getTime() - (offset * 60 * 1000))
//     const date = dateCorrected.toISOString().split('T')[0];
//     const time = dateCorrected.toISOString().split('T')[1].substring(0, 8);
//     return [date, time];
// }

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
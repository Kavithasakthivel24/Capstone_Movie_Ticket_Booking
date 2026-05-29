
function createMovieCard(movie) {

    const statusClass = {
        'NOW_SHOWING': 'badge-now-showing',
        'COMING_SOON': 'badge-coming-soon',
        'TRENDING': 'badge-trending'
    }[movie.status] || 'bg-secondary';

    const statusLabel = (movie.status || '')
        .replace('_', ' ');

    // FIXED POSTER IMAGE
    let poster = movie.posterUrl;

    // If image is empty/null use placeholder
    if (!poster || poster.trim() === '') {
        poster = 'https://via.placeholder.com/300x450/1a1a1a/dc3545?text=No+Poster';
    }

    // If image is just filename like poster.jpg
    // convert to static images folder
    else if (
        !poster.startsWith('http') &&
        !poster.startsWith('/images/')
    ) {
        poster = '/images/' + poster;
    }

    return `
        <div class="col-6 col-sm-4 col-md-3 col-lg-2">

            <div class="movie-card h-100"
                 onclick="window.location.href='/pages/movie-detail.html?id=${movie.id}'"
                 style="cursor:pointer">

                <img
                    src="${poster}"
                    alt="${movie.title}"
                    class="img-fluid movie-poster"
                    loading="lazy"

                    onerror="
                        this.onerror=null;
                        this.src='https://via.placeholder.com/300x450/1a1a1a/dc3545?text=No+Poster';
                    "
                >

                <div class="movie-card-body">

                    <div class="movie-card-title">
                        ${movie.title}
                    </div>

                    <div class="d-flex justify-content-between align-items-center mt-1">

                        <span class="movie-card-info">
                            ${movie.genre || 'N/A'}
                        </span>

                        ${
                            movie.rating
                            ?
                            `
                            <span class="rating-badge">
                                <i class="bi bi-star-fill"></i>
                                ${movie.rating}
                            </span>
                            `
                            :
                            ''
                        }

                    </div>

                    <div class="mt-2">
                        <span class="badge ${statusClass}"
                              style="font-size:0.7rem">
                            ${statusLabel}
                        </span>
                    </div>

                </div>

            </div>

        </div>
    `;
}
async function loadTrendingMovies() {

    const container = document.getElementById('trendingMovies');

    if (!container) return;

    try {

        const response = await fetch('/api/movies/trending');

        if (!response.ok) {
            throw new Error('Failed API');
        }

        const movies = await response.json();

        console.log('Trending Movies:', movies);

        if (!movies || movies.length === 0) {

            container.innerHTML = `
                <div class="col-12 text-center text-muted">
                    No trending movies found
                </div>
            `;

            return;
        }

        container.innerHTML = movies.map(createMovieCard).join('');

    } catch (error) {

        console.error(error);

        container.innerHTML = `
            <div class="col-12 text-center text-danger">
                Failed to load movies
            </div>
        `;
    }
}

async function loadNowShowingMovies() {

    const container = document.getElementById('nowShowingMovies');

    if (!container) return;

    try {

        const response =
            await fetch('/api/movies?status=NOW_SHOWING&size=8');

        if (!response.ok) {
            throw new Error('Failed API');
        }

        const data = await response.json();

        console.log('Now Showing:', data);

        const movies = data.content || [];

        if (movies.length === 0) {

            container.innerHTML = `
                <div class="col-12 text-center text-muted">
                    No movies available
                </div>
            `;

            return;
        }

        container.innerHTML = movies.map(createMovieCard).join('');

    } catch (error) {

        console.error(error);

        container.innerHTML = `
            <div class="col-12 text-center text-danger">
                Failed to load movies
            </div>
        `;
    }
}

function searchMovies() {

    const query =
        document.getElementById('searchInput').value.trim();

    if (query) {

        window.location.href =
            `/pages/movies.html?search=${encodeURIComponent(query)}`;
    }
}

document.addEventListener('keypress', function (e) {

    if (
        e.key === 'Enter' &&
        document.getElementById('searchInput')
    ) {
        searchMovies();
    }
});
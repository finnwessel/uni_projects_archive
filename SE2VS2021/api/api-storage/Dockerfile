FROM mcr.microsoft.com/dotnet/aspnet:6.0 AS base
WORKDIR /app
EXPOSE 80
EXPOSE 443

FROM mcr.microsoft.com/dotnet/sdk:6.0 AS build
WORKDIR /src
COPY NuGet.Config /root/.nuget/NuGet/
COPY ["api-storage/api-storage.csproj", "api-storage/"]
RUN dotnet restore "api-storage/api-storage.csproj"
COPY . .
WORKDIR "/src/api-storage"
RUN dotnet build "api-storage.csproj" -c Release -o /app/build

FROM build AS publish
RUN dotnet publish "api-storage.csproj" -c Release -o /app/publish

FROM base AS final
WORKDIR /app
COPY --from=publish /app/publish .
ENTRYPOINT ["dotnet", "api-storage.dll"]

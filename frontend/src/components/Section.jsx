export default function Section({ title, description, children }) {
  return (
    <section className="section">
      <h2>{title}</h2>
      {description ? <p className="muted">{description}</p> : null}
      {children}
    </section>
  );
}
